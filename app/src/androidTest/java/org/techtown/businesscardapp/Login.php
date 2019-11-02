<?php
    $con = mysqli_connect("localhost", "yujinpark10", "qkr147147!!", "yujinpark10");
    mysqli_query($con,'SET NAMES utf8');

    $userID = $_POST["userID"];
    $userPassword = $_POST["userPassword"];
    
    $statement = mysqli_prepare($con, "SELECT * FROM member WHERE userID = ? AND userPassword = ?");
    mysqli_stmt_bind_param($statement, "ss", $userID, $userPassword);
    mysqli_stmt_execute($statement);


    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $userID, $userPassword, $userName, $userBirth, $userNum, $userEmail);

    $response = array();
    $response["success"] = false;
 
    while(mysqli_stmt_fetch($statement)) {
        $response["success"] = true;
        $response["userID"] = $userID;
        $response["userPassword"] = $userPassword;
        $response["userName"] = $userName;
        $response["userBirth"] = $userBirth;        
        $response["userNum"] = $userNum;
        $response["userEmail"] = $userEmail;
        //$response["cardNum"] = $cardNum;
    }

    echo json_encode($response);


?>