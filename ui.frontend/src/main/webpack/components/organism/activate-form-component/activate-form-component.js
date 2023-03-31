$(document).ready(function () {
  // TODO - make an API call to fetch & display the Personal Information.

  $(".personalEmail__input").eq(0).css("display", "none");
  $(".personalEmail__text").eq(0).css("display", "block");

  $("#submit").on("click", function (event) {
    // TODO - need to make an API call to send all the validated data to backend on clicking 'Submit' button
    event.preventDefault();

    const activateYourAccountFields = [
      "accountNickname",
      "accountNickname",
      "jobcategory",
      "speciality",
      "password",
      "confirmpassword",
      "productinfo",
      "termsofuse",
      "authorizepurchase",
    ];
    const errorMessage = {
      accountNickname: "Account nickname is required",
      jobcategory: "Job Category is required",
      speciality: "Speciality is required",
      password: {
        empty: "Password is required",
        policy: [
          "Must be minimum 8 Characters & less tha 15 Characters",
          "Must include 1 lower case",
          "Must include 1 Upper case",
          "Must include 1 Number",
          "Alpha numeric characters allowed",
          "Only the following special characters are allwoed (@%&$#!*-.)",
        ],
      },
      confirmpassword: "Your new password and confirm password do not match.",
      productinfo:
        "Must be selected, for Product promotion related newsletters",
      termsofuse: 'You must accept the "Terms of Use" and "Terms Conditions".',
      authorizepurchase: "Please confirm you are the authorized account user.",
    };
    activateYourAccountFields.map((value, index) => {
      validateAccountPage(value, errorMessage);
    });
  });
});

/**
 * function trigger/check the validation for each fieldName over the iteration
 * @param {String} fieldName
 * @param {Object} errorMessage
 */
function validateAccountPage(fieldName, errorMessage) {
  switch (fieldName) {
    case "accountNickname":
      let accountNickname = $(".accountNickname");
      let accountNicknameErrorBlock = $(".accountNickname_err");

      Array.from(accountNickname).map((item, index) => {
        const accountNicknameValue = $(item).val().trim();
        if (accountNicknameValue === "" || accountNicknameValue === null) {
          showErrorMessage(accountNicknameErrorBlock[index], errorMessage, fieldName);
        } else {
          removeErrorMessage(accountNicknameErrorBlock[index]);
        }
      });

      break;

    case "jobcategory":
      let jobcategory = $("#jobCategory");
      let jobcategoryErrorBlock = $(".jobcategory_err");
      const jobCategoryValue = $(jobcategory).val();
      if (jobCategoryValue === "" || jobCategoryValue === null) {
        showErrorMessage(jobcategoryErrorBlock, errorMessage, fieldName);
      } else {
        removeErrorMessage(jobcategoryErrorBlock);
      }
      break;

    case "speciality":
      let speciality = $("#speciality");
      let specialityErrorBlock = $(".speciality_err").eq(0);
      const specialityValue = $(speciality).val();
      if (specialityValue === "" || specialityValue === null) {
        showErrorMessage(specialityErrorBlock, errorMessage, fieldName);
      } else {
        removeErrorMessage(specialityErrorBlock);
      }
      break;

    case "password": {
      let passwordField = $("#password");
      const passwordValue = $(passwordField).val();
      let passwordErrBlock = $(".password__err").eq(0);

      if (
        $(passwordField).val().trim() === "" ||
        $(passwordField).val().trim() === null
      ) {
        $(passwordErrBlock).addClass("error_msg");
        $(passwordErrBlock).html(errorMessage[fieldName]?.empty);
      } else {
        $(passwordErrBlock).removeClass("error_msg").css("display", "block");
        $(passwordErrBlock).html("");

        if (!new RegExp("^(?=.{8,14}$)").test(passwordValue)) {
          let errMsg = "Must be minimum 8 Characters & less tha 15 Characters";
          appendPasswordPolicyError(passwordErrBlock, errMsg);
        }

        if (!new RegExp("(?=.*[a-z])").test(passwordValue)) {
          let errMsg = "Must include 1 lower case";
          appendPasswordPolicyError(passwordErrBlock, errMsg);
        }

        if (!new RegExp("(?=.*[A-Z])").test(passwordValue)) {
          let errMsg = "Must include 1 upper case";
          appendPasswordPolicyError(passwordErrBlock, errMsg);
        }

        if (!new RegExp("(?=.*[0-9])").test(passwordValue)) {
          let errMsg = "Must include 1 Number";
          appendPasswordPolicyError(passwordErrBlock, errMsg);
        }

        //working - ^(?=.*[@%&$#!*\-.])[a-zA-Z0-9@%&$#!*\-]+$'
        if (
          !new RegExp("^(?=.*[@%&$#!*-.])[a-zA-Z0-9@%&$#!*.\\-]+$").test(
            passwordValue
          )
        ) {
          let errMsg =
            "Only the following special characters are allwoed (@%&$#!*-.)";
          appendPasswordPolicyError(passwordErrBlock, errMsg);
        }
      }
      break;
    }

    case "confirmpassword": {
      let passwordField = $("#password");
      let confirmPasswordField = $("#confirmpassword");
      let confirmPasswordErrBlock = $(".confirmpassword__err").eq(0);
      const passwordValue = $(passwordField).val().trim();
      const confirmPasswordValue = $(confirmPasswordField).val().trim();
      if (
        (passwordValue !== "" || passwordValue !== null) &&
        passwordValue !== confirmPasswordValue
      ) {
        showErrorMessage(confirmPasswordErrBlock, errorMessage, fieldName);
      } else {
        removeErrorMessage(confirmPasswordErrBlock);
      }
      break;
    }

    case "productinfo":
      let productinfoCheckbox = $("#productinfo");
      let productinfoCheckboxError = $(".product-info__err").eq(0);
      const isproductInfoChecked = $(productinfoCheckbox).is(":checked");
      if (!isproductInfoChecked) {
        showErrorMessage(productinfoCheckboxError, errorMessage, fieldName);
      } else {
        removeErrorMessage(productinfoCheckboxError);
      }
      break;

    case "termsofuse":
      let termsofuseCheckbox = $("#termsofuse");
      let termsofuseCheckboxError = $(".termsofuse__err").eq(0);
      const istermsofuseChecked = $(termsofuseCheckbox).is(":checked");

      if (!istermsofuseChecked) {
        showErrorMessage(termsofuseCheckboxError, errorMessage, fieldName);
      } else {
        removeErrorMessage(termsofuseCheckboxError);
      }
      break;

    case "authorizepurchase":
      let authorizepurchaseCheckbox = $("#authorized");
      let authorizepurchaseCheckboxError = $(".authorized__err");
      const isauthorizePurchaseChecked = $(authorizepurchaseCheckbox).is(
        ":checked"
      );
      if (!isauthorizePurchaseChecked) {
        showErrorMessage(
          authorizepurchaseCheckboxError,
          errorMessage,
          fieldName
        );
      } else {
        removeErrorMessage(authorizepurchaseCheckboxError);
      }
      break;
  }
}

/**
 * function appends the error message to the password field error msg block
 * @param {HTMLElement} errorElement
 * @param {String} errorMessage
 */
function appendPasswordPolicyError(errorElement, errorMessage) {
  $(errorElement).append(
    "<li class='password_error_msg'>" + `${errorMessage}` + "</li>"
  );
}

/**
 * function show the error message for the respective field which is passwed as parameter
 * @param {HTMLElement} errorFieldElement
 * @param {Object} errorMessage
 * @param {String} fieldName
 */
function showErrorMessage(errorFieldElement, errorMessage, fieldName) {
  $(errorFieldElement).addClass("error_msg");
  $(errorFieldElement).html(errorMessage[fieldName]);
}

/**
 * function remove the error message for the corresponding field which is passed as paramaters
 * @param {HTMLElement} errorFieldElement
 */
function removeErrorMessage(errorFieldElement) {
  $(errorFieldElement).removeClass("erro_msg");
  $(errorFieldElement).html("");
}

saveEmail = function (event) {
  let personalEmailText = $(".personalEmail__text").eq(0);
  let personalEmailInput = $(".personalEmail__input").eq(0);
  let personalEmailError = $(".personalEmail__err").eq(0);
  let emailValue = $(personalEmailInput).val().trim();
  let emailErrorMesg = {
    invalidEmail: "it is an invalid email",
    invalidCompanyEmail: " it is an invalid company email",
  };

  // check for valid email
  if (
    new RegExp(
      "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}.([a-zA-Z]{2,})?$"
    ).test(emailValue)
  ) {
    //new RegExp('/^([a-zA-Z0-9\.-_]+)@([a-zA-Z0-9]+).([a-zA-Z]{3,})$/');
    $(personalEmailError).removeClass("error_msg");
    $(personalEmailError).html("");

    // check for valid company email
    let companyEmailValidation = $(personalEmailInput).val().trim().split("@");
    if (
      companyEmailValidation[1].includes("hotmail") ||
      companyEmailValidation[1].includes("gmail") ||
      companyEmailValidation[1].includes("yahoo")
    ) {
      $(personalEmailError).addClass("error_msg");
      $(personalEmailError).html(emailErrorMesg["invalidCompanyEmail"]);
    }
  } else {
    $(personalEmailError).addClass("error_msg");
    $(personalEmailError).html(emailErrorMesg["invalidEmail"]);
  }

  $(personalEmailInput).css("display", "none");
  $(personalEmailText).css("display", "block");
  $(personalEmailText).css({ marginBottom: "0px" });
  $(personalEmailText).html(event.target.value);
};

enablePersonalEmail = function () {
  let personalEmailText = $(".personalEmail__text").eq(0);
  let personalEmailInput = $(".personalEmail__input").eq(0);
  $(personalEmailText).css("display", "none");
  $(personalEmailInput).css("display", "block");
  $(personalEmailInput).focus();
};
