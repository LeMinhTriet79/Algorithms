public class HaversineAlgorithm {

    // Bán kính trung bình của Trái Đất tính bằng Kilômét
    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Tính khoảng cách giữa 2 tọa độ địa lý
     * @param lat1 Vĩ độ điểm 1 (Degree)
     * @param lon1 Kinh độ điểm 1 (Degree)
     * @param lat2 Vĩ độ điểm 2 (Degree)
     * @param lon2 Kinh độ điểm 2 (Degree)
     * @return Khoảng cách tính bằng Kilômét
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {

        // BƯỚC 1: Chuyển đổi Vĩ độ và Kinh độ từ ĐỘ (Degrees) sang RADIAN
        // Đây là bước bắt buộc vì các hàm Math.sin, Math.cos trong Java nhận đầu vào là Radian
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        // BƯỚC 2: Áp dụng công thức tính 'a'
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        // BƯỚC 3: Áp dụng công thức tính 'c'
        // atan2 là hàm lượng giác trả về góc (radian) giữa trục x và điểm (x,y)
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // BƯỚC 4: Nhân với bán kính Trái Đất để ra khoảng cách thực tế
        return EARTH_RADIUS_KM * c;
    }

    // Hàm Main để test thử
    public static void main(String[] args) {
        // Tọa độ Chợ Bến Thành (TP.HCM)
        double latHCM = 10.7725;
        double lonHCM = 106.6980;

        // Tọa độ Hồ Gươm (Hà Nội)
        double latHN = 21.0285;
        double lonHN = 105.8542;

        double distance = calculateDistance(latHCM, lonHCM, latHN, lonHN);

        // Kết quả sẽ rơi vào khoảng ~ 1140 km đường chim bay
        System.out.printf("Khoảng cách đường chim bay từ TP.HCM đến Hà Nội là: %.2f km\n", distance);
    }
}