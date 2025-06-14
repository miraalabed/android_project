<?php
$link = mysqli_connect("localhost", "root", "", "schoolsystem");

if($link === false){
    die("ERROR: Could not connect. " . mysqli_connect_error());
}

function send_json_response($data) {
    header("Content-Type: application/json");
    echo json_encode($data);
    exit;
}

function send_json_error($message, $status_code) {
    http_response_code($status_code);
    send_json_response(["error" => $message]);
}
?>

