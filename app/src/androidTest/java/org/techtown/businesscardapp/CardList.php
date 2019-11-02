<?php 
    $con = mysqli_connect("localhost", "yujinpark10", "qkr147147!!", "yujinpark10");
    //mysqli_query($con,'SET NAMES utf8');
    //$result = mysqli_query($con, "SELECT * FROM card");
    // $result = mysqli_query($con,"SELECT * FROM card, member WHERE member.userID = card.userID");
    $userID = $_POST["userID"];
	$result = mysqli_query($con, "SELECT * FROM card WHERE userID = card.userID");
    //$result = mysqli_query($con, "SELECT * FROM card WHERE userID = card.userID AND card.mine = 1");
    mysqli_stmt_bind_param($statement, "s", $userID);
    mysqli_stmt_execute($statement);
    //$result = mysqli_query($con, "SELECT * FROM card, member WHERE member.userID = card.userID AND card.mine = 0");

    $response = array(); // 배열선언


    while($row = mysqli_fetch_array($result)) {
    	// response 라는 변수명으로 JSON 타입으로 $response 내용을 출력
        array_push($response, array("cardNum"=>$row[0], "name"=>$row[1], "company"=>$row[2], "team"=>$row[3], "position"=>$row[4], "coNum"=>$row[5], "num"=>$row[6], "e_mail"=>$row[7], "faxNum"=>$row[8], "address"=>$row[9], "userID"=>$row[10]));
        	
    }

    echo json_encode(array("response"=>$response));
    mysqli_close($con);
?>