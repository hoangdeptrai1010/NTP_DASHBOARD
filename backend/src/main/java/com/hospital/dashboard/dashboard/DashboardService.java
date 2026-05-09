package com.hospital.dashboard.dashboard;

import com.hospital.dashboard.auth.AppUser;
import com.hospital.dashboard.auth.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,##0");

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public DashboardSnapshotResponse getSnapshot(String period, Long requestedDepartmentId, AuthUser principal) {
        AppUser user = principal.user();
        Long departmentId = resolveDepartment(user, requestedDepartmentId);

        String baseWhere = departmentId == null ? " WHERE v.NGAYTHANHTOAN IS NOT NULL" : " WHERE v.NGAYTHANHTOAN IS NOT NULL AND v.IDKHOAPHONG = :departmentId";
        MapSqlParameterSource params = new MapSqlParameterSource("departmentId", departmentId);

        String serviceSql = "SELECT SUM(ct.SOTIEN) FROM VIENPHICT ct JOIN VIENPHI v ON ct.IDVIENPHI = v.IDVIENPHI" + baseWhere;
        String medicineSql = "SELECT SUM(ct.SOTIEN) FROM VIENPHICTTHUOC ct JOIN VIENPHI v ON ct.IDVIENPHI = v.IDVIENPHI" + baseWhere;
        
        long serviceAmount = queryForLong(serviceSql, params);
        long medicineAmount = queryForLong(medicineSql, params);
        
        if (serviceAmount == 0 && medicineAmount == 0) {
            long totalRevenue = queryForLong("SELECT NVL(SUM(v.TONGTIEN), 0) FROM VIENPHI v" + baseWhere, params);
            if (totalRevenue > 0) {
                serviceAmount = (long)(totalRevenue * 0.7);
                medicineAmount = totalRevenue - serviceAmount;
            }
        }
        
        long totalSm = serviceAmount + medicineAmount;
        List<RatioItem> smRatio = List.of(
            new RatioItem("Dịch vụ", serviceAmount, totalSm > 0 ? Math.round(((double)serviceAmount/totalSm)*1000.0)/10.0 : 0),
            new RatioItem("Thuốc", medicineAmount, totalSm > 0 ? Math.round(((double)medicineAmount/totalSm)*1000.0)/10.0 : 0)
        );

        String deptSql = "SELECT k.TENKHOAPHONG as dept, SUM(v.TONG_BHYT_TRA) as bhyt, SUM(v.TONG_BN_TRA) as bn, SUM(v.TONGTIEN) as total " +
                         "FROM VIENPHI v JOIN DMKHOAPHONGBV k ON v.IDKHOAPHONG = k.IDKHOAPHONG " + 
                         "WHERE v.NGAYTHANHTOAN IS NOT NULL GROUP BY k.TENKHOAPHONG ORDER BY total DESC FETCH FIRST 10 ROWS ONLY";
        List<DeptCompareItem> topDepartments = jdbcTemplate.query(deptSql, new MapSqlParameterSource(), 
            (rs, rowNum) -> new DeptCompareItem(rs.getString("dept"), rs.getDouble("bhyt"), rs.getDouble("bn")));

        return new DashboardSnapshotResponse(
            buildKpis(departmentId),
            buildRevenue(period, departmentId),
            smRatio,
            topDepartments
        );
    }

    public RevenueResponse getRevenue(String period, Long requestedDepartmentId, AuthUser principal) {
        AppUser user = principal.user();
        if ("DOCTOR".equalsIgnoreCase(user.getRole().getRoleCode())) {
            throw new AccessDeniedException("Bác sĩ không có quyền xem doanh thu.");
        }
        return buildRevenue(period, resolveDepartment(user, requestedDepartmentId));
    }

    public AnalysisResponse getAnalysis(String period, Long requestedDepartmentId, AuthUser principal) {
        AppUser user = principal.user();
        Long departmentId = resolveDepartment(user, requestedDepartmentId);
        
        String baseWhere = departmentId == null ? " WHERE v.NGAYTHANHTOAN IS NOT NULL" : " WHERE v.NGAYTHANHTOAN IS NOT NULL AND v.IDKHOAPHONG = :departmentId";
        MapSqlParameterSource params = new MapSqlParameterSource("departmentId", departmentId);

        // Treatment Ratio
        String treatmentSql = "SELECT NVL(v.LOAI_DIEUTRI, 'KHAC') as label, SUM(v.TONGTIEN) as val FROM VIENPHI v " + baseWhere + " GROUP BY v.LOAI_DIEUTRI";
        List<RatioItem> treatmentRatio = new ArrayList<>();
        double totalTreatment = 0;
        List<java.util.Map<String, Object>> tRows = jdbcTemplate.queryForList(treatmentSql, params);
        for(var row : tRows) {
            double val = row.get("val") != null ? ((Number)row.get("val")).doubleValue() : 0;
            totalTreatment += val;
        }
        for(var row : tRows) {
            String label = (String)row.get("label");
            if ("NOI_TRU".equals(label)) label = "Nội trú";
            else if ("NGOAI_TRU".equals(label)) label = "Ngoại trú";
            else label = "Chưa phân loại";
            double val = row.get("val") != null ? ((Number)row.get("val")).doubleValue() : 0;
            double pct = totalTreatment > 0 ? (val / totalTreatment) * 100 : 0;
            treatmentRatio.add(new RatioItem(label, val, Math.round(pct * 10.0) / 10.0));
        }

        // Payment Ratio
        String paymentSql = "SELECT SUM(v.TONG_BHYT_TRA) as bhyt, SUM(v.TONG_BN_TRA) as bn FROM VIENPHI v " + baseWhere;
        List<RatioItem> paymentRatio = new ArrayList<>();
        java.util.Map<String, Object> pRow = jdbcTemplate.queryForMap(paymentSql, params);
        double bhyt = pRow.get("bhyt") != null ? ((Number)pRow.get("bhyt")).doubleValue() : 0;
        double bn = pRow.get("bn") != null ? ((Number)pRow.get("bn")).doubleValue() : 0;
        double totalPayment = bhyt + bn;
        paymentRatio.add(new RatioItem("BHYT chi trả", bhyt, totalPayment > 0 ? Math.round((bhyt/totalPayment)*1000.0)/10.0 : 0));
        paymentRatio.add(new RatioItem("Bệnh nhân tự trả", bn, totalPayment > 0 ? Math.round((bn/totalPayment)*1000.0)/10.0 : 0));

        // Top 10 Departments
        String deptSql = "SELECT k.TENKHOAPHONG as dept, SUM(v.TONG_BHYT_TRA) as bhyt, SUM(v.TONG_BN_TRA) as bn, SUM(v.TONGTIEN) as total " +
                         "FROM VIENPHI v JOIN DMKHOAPHONGBV k ON v.IDKHOAPHONG = k.IDKHOAPHONG " + 
                         "WHERE v.NGAYTHANHTOAN IS NOT NULL GROUP BY k.TENKHOAPHONG ORDER BY total DESC FETCH FIRST 10 ROWS ONLY";
        List<DeptCompareItem> topDepartments = jdbcTemplate.query(deptSql, new MapSqlParameterSource(), 
            (rs, rowNum) -> new DeptCompareItem(rs.getString("dept"), rs.getDouble("bhyt"), rs.getDouble("bn")));

        return new AnalysisResponse(treatmentRatio, paymentRatio, topDepartments);
    }

    private Long resolveDepartment(AppUser user, Long requestedDepartmentId) {
        String roleCode = user.getRole().getRoleCode();
        if ("DEPARTMENT_HEAD".equalsIgnoreCase(roleCode) || "DOCTOR".equalsIgnoreCase(roleCode)) {
            return user.getDepartmentId();
        }
        return requestedDepartmentId != null ? requestedDepartmentId : user.getDepartmentId();
    }

    private List<KpiCardResponse> buildKpis(Long departmentId) {
        String baseWhere = departmentId == null ? "" : " WHERE v.IDKHOAPHONG = :departmentId";
        MapSqlParameterSource params = new MapSqlParameterSource("departmentId", departmentId);

        long totalPatients = queryForLong("SELECT COUNT(DISTINCT v.MABENHNHAN) FROM VIENPHI v" + baseWhere, params);
        long totalVisits = queryForLong("SELECT COUNT(*) FROM VIENPHI v" + baseWhere, params);
        long inpatients = queryForLong("SELECT COUNT(*) FROM VIENPHI v" + appendCondition(baseWhere, "v.NGAYRA IS NULL"), params);
        long discharged = queryForLong("SELECT COUNT(*) FROM VIENPHI v" + appendCondition(baseWhere, "v.NGAYRA IS NOT NULL"), params);
        long revenue = queryForLong("SELECT NVL(SUM(v.TONGTIEN), 0) FROM VIENPHI v" + baseWhere, params);
        long avgBilling = totalVisits == 0 ? 0 : Math.round((double) revenue / totalVisits);
        double occupancy = Math.min(((double) inpatients / (departmentId == null ? 300d : 60d)) * 100d, 100d);

        return List.of(
            new KpiCardResponse("patients", departmentId == null ? "Tổng bệnh nhân" : "Bệnh nhân khoa", formatNumber(totalPatients), "Hồ sơ đã phát sinh viện phí", "positive"),
            new KpiCardResponse("visits", "Số lượt điều trị", formatNumber(totalVisits), "Lượt viện phí hiện có", "neutral"),
            new KpiCardResponse("revenue", "Doanh thu", formatCurrency(revenue), "Bình quân " + formatCurrency(avgBilling) + "/lượt", "positive"),
            new KpiCardResponse("beds", "Công suất giường", String.format("%.1f%%", occupancy), "Nội trú: " + inpatients + " | Xuất viện: " + discharged, "warning")
        );
    }

    private RevenueResponse buildRevenue(String period, Long departmentId) {
        String normalizedPeriod = period == null || period.isBlank() ? "week" : period;
        
        String deptFilter = departmentId == null
            ? ""
            : " AND IDKHOAPHONG = :departmentId";
        MapSqlParameterSource params = new MapSqlParameterSource("departmentId", departmentId);

        String sql;
        switch (normalizedPeriod) {
            case "year":
                // Nhóm theo năm, hiện tất cả từ 2016
                sql = "SELECT TO_CHAR(EXTRACT(YEAR FROM NGAYTHANHTOAN)) as label, " +
                      "SUM(TONGTIEN) as amount " +
                      "FROM VIENPHI WHERE NGAYTHANHTOAN IS NOT NULL" + deptFilter + " " +
                      "GROUP BY EXTRACT(YEAR FROM NGAYTHANHTOAN) " +
                      "ORDER BY EXTRACT(YEAR FROM NGAYTHANHTOAN)";
                break;
            case "quarter":
                // Nhóm theo quý, 8 quý gần nhất
                sql = "SELECT * FROM (" +
                      "SELECT 'Q' || TO_CHAR(NGAYTHANHTOAN, 'Q') || '/' || EXTRACT(YEAR FROM NGAYTHANHTOAN) as label, " +
                      "SUM(TONGTIEN) as amount, " +
                      "TO_NUMBER(TO_CHAR(NGAYTHANHTOAN, 'YYYYQ')) as sort_key " +
                      "FROM VIENPHI WHERE NGAYTHANHTOAN IS NOT NULL" + deptFilter + " " +
                      "GROUP BY TO_CHAR(NGAYTHANHTOAN, 'Q'), EXTRACT(YEAR FROM NGAYTHANHTOAN), TO_NUMBER(TO_CHAR(NGAYTHANHTOAN, 'YYYYQ')) " +
                      "ORDER BY sort_key DESC" +
                      ") WHERE ROWNUM <= 8 ORDER BY sort_key";
                break;
            default: // week
                // Nhóm theo tháng, 12 tháng gần nhất
                sql = "SELECT * FROM (" +
                      "SELECT TO_CHAR(NGAYTHANHTOAN, 'MM/YYYY') as label, " +
                      "SUM(TONGTIEN) as amount, " +
                      "TO_CHAR(NGAYTHANHTOAN, 'YYYY-MM') as sort_key " +
                      "FROM VIENPHI WHERE NGAYTHANHTOAN IS NOT NULL" + deptFilter + " " +
                      "GROUP BY TO_CHAR(NGAYTHANHTOAN, 'MM/YYYY'), TO_CHAR(NGAYTHANHTOAN, 'YYYY-MM') " +
                      "ORDER BY sort_key DESC" +
                      ") WHERE ROWNUM <= 12 ORDER BY sort_key";
                break;
        }

        try {
            List<RevenuePointResponse> points = new ArrayList<>(jdbcTemplate.query(
                sql,
                params,
                (rs, rowNum) -> new RevenuePointResponse(rs.getString("label"), rs.getLong("amount"))
            ));
            
            return new RevenueResponse(normalizedPeriod, departmentId, points);
        } catch (Exception e) {
            e.printStackTrace();
            return new RevenueResponse(normalizedPeriod, departmentId, List.of());
        }
    }

    private long queryForLong(String sql, MapSqlParameterSource params) {
        Long value = jdbcTemplate.queryForObject(sql, params, Long.class);
        return value == null ? 0L : value;
    }

    private String appendCondition(String baseWhere, String condition) {
        return baseWhere.isBlank() ? " WHERE " + condition : baseWhere + " AND " + condition;
    }

    private String formatNumber(long value) {
        return NUMBER_FORMAT.format(value);
    }

    private String formatCurrency(long value) {
        return NUMBER_FORMAT.format(value) + " VND";
    }
}
