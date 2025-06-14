<?php
// api/teacher_subjects.php

require_once 'db_config.php';

// Check if teacher_id is provided
if (!isset($_GET["teacher_id"]) || empty($_GET["teacher_id"])) {
    send_json_error("Teacher ID is required.", 400);
}

$teacher_id = intval($_GET["teacher_id"]);

// Prepare SQL statement to prevent SQL injection
// Option 1: Get subjects directly assigned to the teacher
// $sql = "SELECT subject_id, subject_name FROM subjects WHERE teacher_id = ?";

// Option 2: Get distinct subjects from the teacher's schedule (might be more accurate if subjects.teacher_id isn't maintained)
$sql = "SELECT DISTINCT s.subject_id, s.subject_name 
        FROM subjects s 
        JOIN teacherschedule ts ON s.subject_id = ts.subject_id 
        WHERE ts.teacher_id = ?";

if ($stmt = mysqli_prepare($link, $sql)) {
    // Bind variables to the prepared statement as parameters
    mysqli_stmt_bind_param($stmt, "i", $param_teacher_id);

    // Set parameters
    $param_teacher_id = $teacher_id;

    // Attempt to execute the prepared statement
    if (mysqli_stmt_execute($stmt)) {
        $result = mysqli_stmt_get_result($stmt);
        $subjects = array();
        if (mysqli_num_rows($result) > 0) {
            while ($row = mysqli_fetch_assoc($result)) {
                $subjects[] = $row;
            }
            send_json_response($subjects);
        } else {
            // Send empty array if no subjects found, not necessarily an error
            send_json_response([]); 
        }
    } else {
        send_json_error("Oops! Something went wrong executing statement. Please try again later.", 500);
    }

    // Close statement
    mysqli_stmt_close($stmt);
} else {
    send_json_error("Oops! Something went wrong preparing statement. Please try again later.", 500);
}

// Close connection
mysqli_close($link);

?>
