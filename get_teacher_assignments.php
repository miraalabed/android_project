<?php
header("Content-Type: application/json");
$conn = new mysqli("localhost", "root", "", "schoolsystem");

if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "Connection failed"]);
    exit;
}

$teacher_id = $_GET['teacher_id'] ?? null;

if (!$teacher_id) {
    echo json_encode(["status" => "error", "message" => "Missing teacher_id"]);
    exit;
}

$sql = "SELECT assignment_id, title, description, due_date 
        FROM assignments 
        WHERE teacher_id = ?
        ORDER BY due_date DESC";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $teacher_id);
$stmt->execute();
$result = $stmt->get_result();

$assignments = [];

while ($row = $result->fetch_assoc()) {
    $assignments[] = $row;
}

echo json_encode([
    "status" => "success",
    "data" => $assignments
]);

$stmt->close();
$conn->close();
?>
