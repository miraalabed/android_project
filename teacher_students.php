<?php
// api/teacher_students.php

require_once 'db_config.php';

// Check if required parameters are provided
if (!isset($_GET["teacher_id"]) || empty($_GET["teacher_id"])) {
    send_json_error("Teacher ID is required.", 400);
}
if (!isset($_GET["subject_id"]) || empty($_GET["subject_id"])) {
    send_json_error("Subject ID is required.", 400);
}

$teacher_id = intval($_GET["teacher_id"]);
$subject_id = intval($_GET["subject_id"]);

// Prepare SQL statement to prevent SQL injection
// Select distinct students linked to the teacher and subject via the student schedule
$sql = "SELECT DISTINCT u.id as student_id, u.full_name 
        FROM users u 
        JOIN studentschedule ss ON u.id = ss.student_id 
        WHERE ss.teacher_id = ? AND ss.subject_id = ? AND u.role = 'Student' 
        ORDER BY u.full_name"; // Added ordering

if ($stmt = mysqli_prepare($link, $sql)) {
    // Bind variables to the prepared statement as parameters
    mysqli_stmt_bind_param($stmt, "ii", $param_teacher_id, $param_subject_id);

    // Set parameters
    $param_teacher_id = $teacher_id;
    $param_subject_id = $subject_id;

    // Attempt to execute the prepared statement
    if (mysqli_stmt_execute($stmt)) {
        $result = mysqli_stmt_get_result($stmt);
        $students = array();
        if (mysqli_num_rows($result) > 0) {
            while ($row = mysqli_fetch_assoc($result)) {
                $students[] = $row;
            }
            send_json_response($students);
        } else {
            // Send empty array if no students found for this teacher/subject combination
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
