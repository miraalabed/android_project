<?php
$host = "localhost";
$db = "schoolsystem";
$user = "root";
$pass = "";

$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) {
    die(json_encode([]));
}

$result = $conn->query("SELECT id as survey_id, title as survey_title, date as created_at FROM surveys");

$surveys = [];
while ($row = $result->fetch_assoc()) {
    $surveys[] = $row;
}

header('Content-Type: application/json');
echo json_encode($surveys);
$conn->close();
?>
