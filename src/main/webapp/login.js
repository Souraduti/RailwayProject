document.getElementById("loginForm").addEventListener("submit",(e)=>{
    e.preventDefault();
    const formdata = new FormData(e.target);
    const params = new URLSearchParams(formdata).toString();
    const url = "/railwayProject/webapi/root/login";
    fetch(url,{
        method:'POST',
        headers:{
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: params
    })
    .then((response)=>{
        if(response.status!=200){
            return null;
        }
        return response.json();
    })
    .then((value)=>{
        if(value.login_status=="failed"){
            console.log(value);
            alert("Wrong username or Password\n");
        }else{
            console.log(value.token);
            window.sessionStorage.setItem("token",value.token);
            window.history.back();
        }
    })
    .catch(console.log);
});
document.getElementById("signupButton").addEventListener("click",(e)=>{
    e.preventDefault();
    window.location.href ="/railwayProject/signup.html";
});

