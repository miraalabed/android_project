<?php
header("Content-Type: application/json; charset=UTF-8");
$conn = new mysqli("localhost", "root", "", "schoolsystem");

if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "Connection failed"]);
    exit;
}

// التقاط البيانات بصيغة JSON
$data = json_decode(file_get_contents("php://input"), true);

$id = $data['id'] ?? null;
$name = $data['name'] ?? '';
$email = $data['email'] ?? '';
$phone = $data['phone'] ?? '';
$address = $data['address'] ?? '';

if (!$id) {
    echo json_encode(["status" => "error", "message" => "Missing student ID"]);
    exit;
}

$stmt = $conn->prepare("UPDATE students SET name=?, email=?, phone=?, address=? WHERE id=?");
$stmt->bind_param("ssssi", $name, $email, $phone, $address, $id);

if ($stmt->execute()) {
    echo json_encode([
        "status" => "success",
        "message" => $stmt->affected_rows > 0 ? "Student info updated" : "No changes made"
    ]);
} else {
    echo json_encode(["status" => "error", "message" => "Update failed", "error" => $stmt->error]);
}

$stmt->close();
$conn->close();
?>
