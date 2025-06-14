<?php
header("Content-Type: application/json; charset=UTF-8");
ini_set('display_errors', 1);
error_reporting(E_ALL);

$host = "localhost";
$db = "schoolsystem";  // ← غيّر حسب اسم قاعدة البيانات عندك
$user = "root";
$pass = "";             // ← كلمة مرور MySQL

$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "Connection failed"]);
    exit();
}

$sql = "SELECT * FROM announcements ORDER BY date DESC";
$result = $conn->query($sql);

$data = [];
while ($row = $result->fetch_assoc()) {
    $data[] = [
        "title" => $row["title"],
        "content" => $row["content"],
        "date" => $row["date"]
    ];
}

echo json_encode(["status" => "success", "data" => $data]);
$conn->close();
?>
