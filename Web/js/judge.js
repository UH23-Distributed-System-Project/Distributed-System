!function checkLogin () {
    $.ajaxSetup( {
        // Set the execution action after the ajax request ends
        complete : function(XMLHttpRequest, textStatus) {
            // Get the information in the response header through XMLHttpRequest to determine whether it is a redirection
            var redirect = XMLHttpRequest.getResponseHeader("REDIRECT");
            if (redirect == "REDIRECT") {
                alert("Not logged in, please log in first");
                // Get the path and redirect
               window.top.location.href= XMLHttpRequest.getResponseHeader("PATH");
            }
        }
    });
}();
