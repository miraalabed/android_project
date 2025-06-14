<?php
header("Content-Type: application/json");

$host = "localhost";
$db = "schoolsystem";
$user = "root";
$pass = "";

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "Connection failed"]);
    exit();
}

$survey_id = $_GET['survey_id'] ?? 0;

$stmt = $conn->prepare("SELECT question FROM surveys WHERE id = ?");
$stmt->bind_param("i", $survey_id);
$stmt->execute();
$result = $stmt->get_result();

if ($row = $result->fetch_assoc()) {
    echo json_encode(["status" => "success", "question" => $row['question']]);
} else {
    echo json_encode(["status" => "error", "message" => "Survey not found"]);
}

$stmt->close();
$conn->close();
?>
