const fs = require("fs");
const path = require("path");

const registerPartials = (handlebarInstance) => {

  handlebarInstance.registerHelper("math", function (lvalue, operator, rvalue) {
    lvalue = parseFloat(lvalue);
    rvalue = parseFloat(rvalue);

    const result = {
      "+": lvalue + rvalue,
      "-": lvalue - rvalue,
      "*": lvalue * rvalue,
      "/": lvalue / rvalue,
      "%": lvalue % rvalue,
      "<": lvalue < rvalue,
      "<=": lvalue <= rvalue,
      ">=": lvalue >= rvalue,
      ">": lvalue > rvalue,
      "===": lvalue === rvalue,
    }[operator];

    return result;
  });

  /**
   * Helper for handlebars, this will print the object JSON for passed object.
   * @example {{getObjectJSON object}}
   */
  handlebarInstance.registerHelper("getObjectJSON", function (obj) {
    return JSON.stringify(obj);
  });

  /**
   * Helper for handlebars, this will return true for false inputs.
   * @example {{not object}}
   */
  handlebarInstance.registerHelper("not", function (input) {
    return !input;
  });


  try {
    let registerPartial = (directoryPath) => {
      fs.readdirSync(`./src/main/webpack/${directoryPath}`).forEach((dir) => {
          if(dir === 'atom' || dir === 'molecule' || dir === 'organism'){
          fs.readdirSync(`./src/main/webpack/${directoryPath}/${dir}`).forEach((comp) => {
              const partialTemplate = fs.readFileSync(
                path.resolve("src/main/webpack/", directoryPath, dir, comp, `${comp}.hbs`),
                "utf8"
              );
              handlebarInstance.registerPartial(`oca-${comp}`, partialTemplate);
          });
        }
        
      });
    }
    registerPartial('components');
  } catch (err) {
    console.log(err)
  }
}

module.exports = (handlebarInstance) => {
  return registerPartials(handlebarInstance);
}
