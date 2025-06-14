<?php
include("db_connection.php");

header("Content-Type: application/json");

if (isset($_POST["id"])) {
    $id = $_POST["id"];
    $full_name = $_POST["full_name"];
    $email = $_POST["email"];
    $address = $_POST["address"];

    $stmt = $conn->prepare("UPDATE users SET full_name = ?, email = ?, address = ? WHERE id = ?");
    $stmt->bind_param("sssi", $full_name, $email, $address, $id);

    if ($stmt->execute()) {
        echo json_encode(["success" => true]);
    } else {
        echo json_encode(["success" => false, "error" => $stmt->error]);
    }

    $stmt->close();
    $conn->close();
} else {
    echo json_encode(["success" => false, "error" => "Missing parameters"]);
}
?>
