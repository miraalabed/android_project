<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");

$student_id = $_GET['student_id'] ?? null;

if (!$student_id) {
    echo json_encode(["success" => false, "message" => "Missing student_id"]);
    exit;
}

$conn = new mysqli("localhost", "root", "", "schoolsystem");

if ($conn->connect_error) {
    echo json_encode(["success" => false, "message" => "Database connection failed"]);
    exit;
}

$sql = "SELECT id, sender_id, sender_type, title, content, sent_at 
        FROM messages 
        WHERE receiver_id = ? AND receiver_type = 'Student'
        ORDER BY sent_at DESC";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $student_id);
$stmt->execute();
$result = $stmt->get_result();

$messages = [];

while ($row = $result->fetch_assoc()) {
    $messages[] = $row;
}

echo json_encode([
    "success" => true,
    "messages" => $messages
]);

$stmt->close();
$conn->close();
?>
