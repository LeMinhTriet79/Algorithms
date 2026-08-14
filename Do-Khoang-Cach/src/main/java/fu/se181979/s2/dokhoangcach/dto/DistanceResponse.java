package fu.se181979.s2.dokhoangcach.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistanceResponse {
    private String origin;          // Địa chỉ xuất phát
    private String destination;     // Địa chỉ đích đến
    private double distanceKm;      // Khoảng cách theo Haversine
    private String message;         // Lời nhắn (ví dụ: Thành công / Thất bại)
}