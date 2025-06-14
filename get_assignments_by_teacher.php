<?php
header('Content-Type: application/json');
header("Access-Control-Allow-Origin: *");

$conn = new mysqli("localhost", "root", "", "schoolsystem");
if ($conn->connect_error) {
    die(json_encode(["status" => "error", "message" => "Connection failed"]));
}

$teacher_id = isset($_GET['teacher_id']) ? intval($_GET['teacher_id']) : 0;
if ($teacher_id <= 0) {
    echo json_encode([]);
    exit;
}

$sql = "SELECT assignment_id, title FROM assignments WHERE teacher_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $teacher_id);
$stmt->execute();
$result = $stmt->get_result();

$assignments = [];
while ($row = $result->fetch_assoc()) {
    $assignments[] = $row;
}

echo json_encode($assignments);

$stmt->close();
$conn->close();
?>
