
function enableList(para) {
    if (para.checked) {
        document.getElementById("list").disabled = false;
    } else {
        document.getElementById("list").disabled = true;
    }
}

function checkFiles() {
    var file = document.getElementById("upload_form").files.value;
    if (file == null || file == "") {
        alert("Please select at least a file");
        return;
    }
    document.getElementById("upload_form").submit();
}