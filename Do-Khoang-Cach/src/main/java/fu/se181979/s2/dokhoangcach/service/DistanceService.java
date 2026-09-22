package fu.se181979.s2.dokhoangcach.service;

import fu.se181979.s2.dokhoangcach.dto.Coordinate;
import org.springframework.stereotype.Service;

/**
 * Service này chịu trách nhiệm thực thi Thuật toán Haversine.
 * Mục đích: Tính toán khoảng cách ngắn nhất (đường chim bay) giữa 2 điểm trên bề mặt hình cầu (Trái Đất).
 */
@Service // Đánh dấu là Spring Bean để tái sử dụng ở các class khác
public class DistanceService {

    // Bán kính trung bình của Trái Đất (đơn vị: Kilômét).
    // Hằng số này cực kỳ quan trọng trong công thức.
    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Hàm tính khoảng cách giữa 2 điểm địa lý
     * @param point1 Tọa độ điểm xuất phát (gồm vĩ độ, kinh độ)
     * @param point2 Tọa độ điểm đích đến (gồm vĩ độ, kinh độ)
     * @return Khoảng cách đường chim bay (đơn vị: km)
     */
    public double calculateHaversineDistance(Coordinate point1, Coordinate point2) {

        // BƯỚC 1: Đổi đơn vị
        // API OpenStreetMap trả về tọa độ ở dạng Độ (Degree).
        // Tuy nhiên, các hàm lượng giác của thư viện Math trong Java yêu cầu đầu vào phải là Radian.
        double lat1Rad = Math.toRadians(point1.getLat());
        double lon1Rad = Math.toRadians(point1.getLon());
        double lat2Rad = Math.toRadians(point2.getLat());
        double lon2Rad = Math.toRadians(point2.getLon());

        // Tính độ chênh lệch (Delta) giữa 2 tọa độ (tính theo Radian)
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // BƯỚC 2: Tính biến 'a' (Bình phương độ dài nửa dây cung đâm xuyên qua quả cầu)
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        // BƯỚC 3: Tính biến 'c' (Góc ở tâm Trái Đất)
        // atan2 là hàm an toàn giúp tránh lỗi mất mát độ chính xác khi 2 điểm ở quá gần nhau
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // BƯỚC 4: Tính kết quả cuối cùng (Khoảng cách d)
        // Độ dài cung tròn = Bán kính (R) * Góc ở tâm (c)
        return EARTH_RADIUS_KM * c;
    }
}