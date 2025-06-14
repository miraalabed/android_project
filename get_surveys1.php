<?php
header("Content-Type: application/json; charset=UTF-8");

// إعدادات الاتصال
$host = "localhost";
$db = "schoolsystem";
$user = "root";
$pass = "";

// إنشاء الاتصال
$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Connection failed: " . $conn->connect_error]);
    exit();
}

// التحقق من رقم الطالب
$student_id = isset($_GET['student_id']) ? intval($_GET['student_id']) : 0;
if ($student_id <= 0) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Invalid student ID"]);
    exit();
}

// تنفيذ الاستعلام
$sql = "SELECT id, title, status, date FROM surveys WHERE student_id = ?";
$stmt = $conn->prepare($sql);
if (!$stmt) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Prepare failed"]);
    exit();
}

$stmt->bind_param("i", $student_id);
$stmt->execute();
$result = $stmt->get_result();

// تجهيز النتائج
$surveys = [];
while ($row = $result->fetch_assoc()) {
    $surveys[] = $row;
}

// إرجاع النتيجة
echo json_encode([
    "status" => "success",
    "surveys" => $surveys
]);

$stmt->close();
$conn->close();
?>