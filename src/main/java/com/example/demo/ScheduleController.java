package com.example.demo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class ScheduleController {

    private final JdbcTemplate jdbcTemplate;

    public ScheduleController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("groups",
                jdbcTemplate.queryForList("SELECT group_id, group_name FROM student_groups ORDER BY group_name"));
        model.addAttribute("days",
                jdbcTemplate.queryForList("SELECT day_id, day_name FROM week_days ORDER BY day_id"));
        return "index";
    }

    @GetMapping("/schedule")
    public String schedule(
            @RequestParam int groupId,
            @RequestParam int dayId,
            Model model
    ) {
        String sql = """
            SELECT ts.start_time, ts.end_time,
                   s.subject_name,
                   t.full_name AS teacher,
                   c.room_number,
                   lt.type_name
            FROM schedule sch
            JOIN time_slots ts ON sch.time_slot_id = ts.time_slot_id
            JOIN subjects s ON sch.subject_id = s.subject_id
            JOIN teachers t ON sch.teacher_id = t.teacher_id
            JOIN classrooms c ON sch.classroom_id = c.classroom_id
            JOIN lesson_types lt ON sch.type_id = lt.type_id
            WHERE sch.group_id = ? AND sch.day_id = ?
            ORDER BY ts.start_time
        """;

        List<Map<String, Object>> lessons =
                jdbcTemplate.queryForList(sql, groupId, dayId);

        model.addAttribute("lessons", lessons);
        return "schedule";
    }
}