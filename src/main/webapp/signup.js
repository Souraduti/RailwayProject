function verify(){
    const emailRegex = /^[\w\.]+@[\w]+\.[\w]+$/;
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[\d\W]).{8,}$/;
    let email = document.getElementById("email");
    if(!emailRegex.test(email.value)){
        alert("Wrong Email Format\n");
        return false;
    }

    let password = document.getElementById("password");
    let cpassword = document.getElementById("confirm");
    if(password.value!==cpassword.value){
        password.value = "";
        cpassword.value = "";
        alert("Please fill same password in both the fileds\n");
        return false;
    }
    if(!passwordRegex.test(password.value)){
        password.value = "";
        cpassword.value = "";
        alert("Password Must have"+
                "At least one uppercase letter\n"+
                "At least one lowercase letter\n"+
                "At least one digit or special symbol\n"+
                "Length at least 8\n");
        return false;
    }
    return true;
}
document.getElementById("signupForm").addEventListener("submit",(e)=>{
    e.preventDefault();
    if(!verify()) return;
    const formdata = new FormData(e.target);
    const params = (new URLSearchParams(formdata)).toString();
    let url = "/railwayProject/webapi/root/signup";
    fetch(url,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: params
      }
    ).then((response)=>{
        if(response.status!=200){
            return null;
        }
        return response.json();
      })
      .then((value)=>{
        console.log(value);
        if(value.signup_status==="failed"){
            alert(value.cause);
        }else{
            window.history.back();
        }
      })
      .catch((e)=>{
        console.log(params);
        alert("Something Went Wrong");
        console.log(e);
      });
});