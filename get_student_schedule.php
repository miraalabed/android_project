<?php
header("Content-Type: application/json");

$conn = new mysqli("localhost", "root", "", "schoolsystem", 3307);

if ($conn->connect_error) {
    die(json_encode(["status" => "error", "message" => "Connection failed"]));
}

$class_grade = 2;

$sql = "SELECT * FROM class_schedule WHERE class_grade = ? ORDER BY FIELD(day, 'Sunday','Monday','Tuesday','Wednesday','Thursday','Friday','Saturday'), period_number";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $class_grade);
$stmt->execute();
$result = $stmt->get_result();

$data = [];
while ($row = $result->fetch_assoc()) {
    $data[] = $row;
}

echo json_encode(["status" => "success", "data" => $data]);
?>
