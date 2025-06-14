<?php
header("Content-Type: application/json");

// بيانات الاتصال بقاعدة البيانات
$host = "localhost";
$db = "schoolsystem";
$user = "root";
$pass = "";

// الاتصال
$conn = new mysqli($host, $user, $pass, $db);

// التحقق من الاتصال
if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "Connection failed"]);
    exit();
}

// استقبال الصف من الطلب (GET parameter)
$class_grade = $_GET['class_grade'] ?? '';

if (empty($class_grade)) {
    echo json_encode(["status" => "error", "message" => "Missing class grade"]);
    exit();
}

// جلب الموارد حسب الصف
$sql = "SELECT title, description, file_link, date AS created_at FROM learning_resources WHERE class_grade = ? ORDER BY date DESC";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $class_grade);
$stmt->execute();
$result = $stmt->get_result();

$data = [];
while ($row = $result->fetch_assoc()) {
    $data[] = $row;
}

echo json_encode(["status" => "success", "data" => $data]);

$stmt->close();
$conn->close();
?>
