 function checkLoginUser() {
    $.ajax({
        type: "POST",
        url: "http://localhost:8020/api/login.php",
        data: {
            username: $("#username").val(),
            password: $("#password").val()
        },
        dataType: "json",
        xhrFields: {
            withCredentials: true
        }
    }).done(function (response) {
        debugger;
        $("#log").html(response.message);
        window.location.href = "article.html";
    }).fail(function (jqXHR, textStatus) {
        console.log("Request failed: " + textStatus);
    });
 }
 
 // Get user login status：Used to limit forced login
 function getLoginStatus() {
    $.ajax({
        type: 'get',
        url: 'login',
        success: function(body) {
            // 200 Login status, no processing is performed
            console.log("当前用户已经登录！")
        },
        error: function() {
            // Jump to login.html page
            console.log("当前用户尚未登录！");
            location.assign("login.html");
        }
    });
}

// Get the current user’s login information
function getUserInfo() {
    $.ajax({
        // TODO::add java code
        type: 'get',
        url: 'userInfo',
        success: function(body) {
            // If the acquisition is successful, the body is a User object.
            // Here fill in the user name
            let h3 = document.querySelector(".container-left>.card>h3");
            h3.innerHTML = body.userName;
        }
    });
}

function qryUserInfoByCon() {
    $.ajax({
        // TODO::add java code
        type: 'get',
        url: 'userInfo' + location.search,
        success: function(body) {
            let h3 = document.querySelector(".container-left>.card>h3");
            h3.innerHTML = body.userName;
       } 
    });
}

// Get article list information
function getArticles() {
    $.ajax({
        // TODO::add java code
        type: 'get',
        url: 'http://127.0.0.1:9090/article',
        data: {
            type: $("#username").val()
        },
        dataType: "json",
        success: function(body) {
            let container = document.querySelector('.container-right');
            /** If the acquisition is successful, the body is an array of js objects, 
            and each element is an article information. **/
            for (let article of body) {
                // Construct article div
                let articleDiv = document.createElement('div');
                articleDiv.className = 'article';

                // Construct article title
                let titleDiv = document.createElement('div');
                titleDiv.className = 'title';
                titleDiv.innerHTML = article.title;
                // Construct article date
                let dateDiv = document.createElement('div');
                dateDiv.className = 'date';
                dateDiv.innerHTML = article.datastr;
                // Construct article description
                let summaryDiv = document.createElement('div');
                summaryDiv.className = 'desc';
                summaryDiv.innerHTML = article.desc;
                // Construct the button
                let a = document.createElement('a');
                a.href = 'article_detail.html?articleId=' + article.id;
                a.innerHTML = 'View Full Test &gt;&gt;';

                // Assembling the final result
                articleDiv.appendChild(titleDiv);
                articleDiv.appendChild(dateDiv);
                articleDiv.appendChild(summaryDiv);
                articleDiv.appendChild(a);
                container.appendChild(articleDiv);
            }
        }
    });
}

function qryArticleByCon() {
    $.ajax({
        type: 'get',
        url: 'article' + location.search,
        success: function (body) {
            let h3 = document.querySelector('.article_detail>h3');
            h3.innerHTML = body.title;
            let dateDiv = document.querySelector('.article_detail>.date');
            dateDiv.innerHTML = body.postTime;
            // let contentDiv = document.querySelector('.article_detail>#content');
            // contentDiv.innerHTML = body.content;

            // 此处使用editor.md来进行渲染
            editormd.markdownToHTML('content', { markdown: body.content });
        }
    });
}


// 更新删除的路径（加上articleId）
function updateDeleteURL() {
    let deleteButton = document.querySelector("#delete_button");
    deleteButton.href = "article_delete" + location.search;
}