function showConfirmationDialog() {
    $("#dialog-confirm").html("Are you sure you want to do this ?");

    // Define the Dialog and its properties.
    $("#dialog-confirm").dialog({
        resizable: false,
        modal: true,
        title: "Mark New Image as Base image ?",
        height: 200,
        width: 500,
        buttons: {
            "Yes": function() {
                $(this).dialog('close');
                callback(true);
            },
            "No": function() {
                $(this).dialog('close');
                callback(false);
            }
        }
    });
}

function callback(value) {
    if (value) {
        var validate = $(location).attr('href').replace('#', '') + '/validate';
        $.post(validate);
        $('.asExpected').text("Images have been reviewed");
        $('.asExpected').attr("class","reviewed");
        $(".reviewed").off("click")
    } else {
        //        alert("Rejected");
    }
}
