<?php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");

// بيانات الاتصال
$conn = new mysqli("localhost", "root", "", "schoolsystem");

if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "Connection failed"]);
    exit;
}

$student_id = $_GET['student_id'] ?? null;

if (!$student_id) {
    echo json_encode(["status" => "error", "message" => "Missing student_id"]);
    exit;
}

// الاستعلام المرتبط بجدول المواد
$sql = "SELECT m.mark_id, m.exam_name, m.mark, m.date, s.subject_name
        FROM marks m
        JOIN subjects s ON m.subject_id = s.subject_id
        WHERE m.student_id = ?";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $student_id);
$stmt->execute();
$result = $stmt->get_result();

$grades = [];

while ($row = $result->fetch_assoc()) {
    $grades[] = [
        "id" => $row['mark_id'],
        "subject_name" => $row['subject_name'],
        "exam_name" => $row['exam_name'],
        "mark" => $row['mark'],
        "date" => $row['date']
    ];
}

// إخراج النتائج بصيغة JSON
echo json_encode([
    "status" => "success",
    "data" => $grades
]);

$stmt->close();
$conn->close();
?>