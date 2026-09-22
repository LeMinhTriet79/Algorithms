package fu.se181979.s2.dokhoangcach.controller;

import fu.se181979.s2.dokhoangcach.dto.Coordinate;
import fu.se181979.s2.dokhoangcach.dto.DistanceResponse;
import fu.se181979.s2.dokhoangcach.service.DistanceService;
import fu.se181979.s2.dokhoangcach.service.GeocodingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller này cung cấp API (đường dẫn web) để người dùng bên ngoài có thể gọi vào hệ thống.
 */
@RestController // Báo cho Spring Boot biết đây là nơi nhận các luồng gọi API và trả về JSON
@RequestMapping("/api/v1/khoang-cach") // Đường dẫn gốc của API này
public class DistanceController {

    // Khai báo 2 "thuộc cấp" Service để Controller sai bảo
    private final GeocodingService geocodingService;
    private final DistanceService distanceService;

    // Kỹ thuật Dependency Injection: Spring Boot sẽ tự động "bơm" 2 Service vào Controller
    @Autowired
    public DistanceController(GeocodingService geocodingService, DistanceService distanceService) {
        this.geocodingService = geocodingService;
        this.distanceService = distanceService;
    }

    /**
     * API tính khoảng cách.
     * Cách gọi: GET /api/v1/khoang-cach/tinh-toan?diemDi=Địa_chỉ_A&diemDen=Địa_chỉ_B
     */
    @GetMapping("/tinh-toan")
    public ResponseEntity<DistanceResponse> calculateDistance(
            @RequestParam("diemDi") String diemDi,
            @RequestParam("diemDen") String diemDen) {

        try {
            // Bước 1: Gọi API OpenStreetMap để biến chuỗi địa chỉ thành Tọa độ (Vĩ độ, Kinh độ)
            Coordinate coord1 = geocodingService.getCoordinates(diemDi);
            Coordinate coord2 = geocodingService.getCoordinates(diemDen);

            // Bước 2: Truyền tọa độ vào thuật toán Haversine để tính khoảng cách
            double distance = distanceService.calculateHaversineDistance(coord1, coord2);

            // Bước 3: Làm tròn khoảng cách lấy 2 chữ số thập phân cho đẹp (VD: 1140.56 km)
            double roundedDistance = Math.round(distance * 100.0) / 100.0;

            // Bước 4: Đóng gói kết quả thành DTO và trả về với mã HTTP 200 (Thành công)
            DistanceResponse response = new DistanceResponse(diemDi, diemDen, roundedDistance, "Thành công!");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Nếu có lỗi (VD: không tìm thấy địa chỉ), trả về mã HTTP 400 (Lỗi người dùng) kèm thông báo
            DistanceResponse errorResponse = new DistanceResponse(diemDi, diemDen, 0.0, "Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}