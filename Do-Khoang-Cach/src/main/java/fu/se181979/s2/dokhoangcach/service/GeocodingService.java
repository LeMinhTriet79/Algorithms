package fu.se181979.s2.dokhoangcach.service;

import fu.se181979.s2.dokhoangcach.dto.Coordinate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Service này chịu trách nhiệm giao tiếp với API bên ngoài (OpenStreetMap Nominatim)
 * để chuyển đổi một địa chỉ dạng văn bản (String) thành tọa độ địa lý (Vĩ độ, Kinh độ).
 */
@Service // Đánh dấu đây là một Bean để Spring Boot tự động quản lý (Dependency Injection)
public class GeocodingService {

    // Khởi tạo HttpClient một lần duy nhất để tái sử dụng, giúp tăng hiệu năng
    // Thiết lập timeout 10 giây để tránh việc hệ thống bị treo nếu API bên thứ 3 phản hồi chậm
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // ObjectMapper dùng để parse chuỗi JSON trả về từ API thành Object của Java
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Hàm lấy tọa độ từ địa chỉ.
     * @param address Địa chỉ người dùng nhập vào (VD: "Chợ Bến Thành, TP.HCM")
     * @return Đối tượng Coordinate chứa Vĩ độ (lat) và Kinh độ (lon)
     * @throws Exception Bắn ra lỗi nếu không gọi được API hoặc không tìm thấy địa chỉ
     */
    public Coordinate getCoordinates(String address) throws Exception {

        // 1. Mã hóa địa chỉ (URL Encoding)
        // Vì địa chỉ có thể chứa khoảng trắng và dấu tiếng Việt (VD: "Hồ Chí Minh"),
        // ta phải mã hóa nó để URL hợp lệ (VD: khoảng trắng biến thành %20 hoặc dấu +).
        String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8.toString());

        // 2. Tạo URL gọi API của Nominatim (OpenStreetMap)
        // format=json: Yêu cầu trả về dữ liệu dạng JSON.
        // limit=1: Chỉ lấy kết quả có độ chính xác cao nhất (kết quả đầu tiên).
        String url = "https://nominatim.openstreetmap.org/search?q=" + encodedAddress + "&format=json&limit=1";

        // 3. Xây dựng HTTP Request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                // BẮT BUỘC: Nominatim yêu cầu header User-Agent. Nếu không có, họ sẽ block request của em.
                // Thầy đã điền sẵn format chuẩn, em có thể đổi email nếu muốn.
                .header("User-Agent", "DoKhoangCach-SE181979 (trietleminh7979@gmail.com)")
                .GET()
                .build();

        // 4. Gửi Request và nhận Response
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // 5. Phân tích dữ liệu JSON trả về
        JsonNode rootNode = objectMapper.readTree(response.body());

        // Nếu mảng JSON trả về có phần tử (tức là tìm thấy địa chỉ)
        if (rootNode.isArray() && rootNode.size() > 0) {
            JsonNode firstResult = rootNode.get(0);

            // Lấy value của trường "lat" và "lon" trong JSON
            double lat = firstResult.get("lat").asDouble();
            double lon = firstResult.get("lon").asDouble();

            // Trả về đối tượng Coordinate DTO mà ta đã định nghĩa ở Phần 2
            return new Coordinate(lat, lon);
        } else {
            // Nếu không tìm thấy, ném ra lỗi để Controller xử lý và báo cho người dùng
            throw new Exception("Không tìm thấy tọa độ cho địa chỉ: " + address);
        }
    }
}