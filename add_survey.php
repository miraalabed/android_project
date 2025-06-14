<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
include("db_config.php");

$title = $_POST['title'];
$question = $_POST['question'];
$student_id = $_POST['student_id'];
$date = $_POST['date'];
$status = $_POST['status'];

$sql = "INSERT INTO surveys (title, question, student_id, date, status) 
        VALUES (?, ?, ?, ?, ?)";
$stmt = $link->prepare($sql);
$stmt->bind_param("ssiss", $title, $question, $student_id, $date, $status);

if ($stmt->execute()) {
    echo json_encode(["status" => "success"]);
} else {
    echo json_encode(["status" => "error"]);
}
?>
