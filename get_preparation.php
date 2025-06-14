<?php
header("Content-Type: application/json");

// بيانات الاتصال بقاعدة البيانات
$host = "localhost";
$db = "schoolsystem";
$user = "root";
$pass = "";

// الاتصال
$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "Connection failed"]);
    exit();
}

$class_grade = $_GET['class_grade'] ?? '';

$sql = "SELECT subject_name, title, content, date FROM preparation_plans WHERE class_grade = ? ORDER BY date DESC";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $class_grade);
$stmt->execute();
$result = $stmt->get_result();

$data = [];
while ($row = $result->fetch_assoc()) {
    $data[] = $row;
}

echo json_encode([
    "status" => "success",
    "data" => $data
]);
?>
