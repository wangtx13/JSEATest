
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

function enableCustomizePackage(para) {
    if (para.checked) {
        document.getElementById("customizePackage").disabled = false;
    } else {
        document.getElementById("customizePackage").disabled = true;
    }
}

function enableTextArea(para) {
    if (para.checked) {
        document.getElementById("textarea").disabled = false;
    } else {
        document.getElementById("textarea").disabled = true;
    }
}