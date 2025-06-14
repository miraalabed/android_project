<?php
ini_set('display_errors', 1);
error_reporting(E_ALL);

header("Content-Type: application/json");

// ✅ الاتصال بقاعدة البيانات (بدون تحديد البورت)
$conn = new mysqli("localhost", "root", "", "schoolsystem");

if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "Connection failed"]);
    exit();
}

// استقبال معرف المستقبل (الطالب)
$receiver_id = isset($_GET['receiver_id']) ? intval($_GET['receiver_id']) : 0;

if ($receiver_id <= 0) {
    echo json_encode(["status" => "error", "message" => "Missing or invalid receiver_id"]);
    exit();
}

// الاستعلام عن الرسائل
$sql = "SELECT id, title, content, sender_type, sent_at, sender_id 
        FROM messages 
        WHERE receiver_id = ?";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $receiver_id);
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