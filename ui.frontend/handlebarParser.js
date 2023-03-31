const Handlebars = require("handlebars");
const fs = require('fs');
const registerPartials = require("./registerPartials.js");
const path = require("path");

const handlebarParser = (env) => {
  registerPartials(Handlebars);
  let indexObj = {
    components: {
      atom: [],
      molecule: [],
      organism: [],
    }
  };
  try {
    let handlebarParse = (directoryPath) => {
      fs.readdirSync(`./src/main/webpack/${directoryPath}`).forEach((dir) => {
        if (dir === 'atom' || dir === 'molecule' || dir === 'organism') {
          fs.readdirSync(`./src/main/webpack/${directoryPath}/${dir}`).forEach((comp) => {
            let template = fs.readFileSync(`src/main/webpack/${directoryPath}/${dir}/${comp}/${comp}.hbs`, 'utf8');
            let templateData = fs.readFileSync(`src/main/webpack/${directoryPath}/${dir}/${comp}/${comp}.config.json`, 'utf8');
            let compiledTemplate = Handlebars.compile(template);
            let configJson = JSON.parse(templateData);
            let parsedTemplate = "";
            configJson.variations.forEach((value, index) => {
              parsedTemplate = parsedTemplate + compiledTemplate(value.config);
            })
            createPages(directoryPath, dir, comp, parsedTemplate);
          })
        }
      })
    }

    const createPages = (directoryPath, dir, comp, parsedTemplate) => {
      const html = fs.readFileSync('src/main/webpack/commons/fe-hbs-templates/hbs-index.html', "utf8");
      const file = html.replace(
        /<div id="root">(.*)<\/div>/,
        `<div id="root">${parsedTemplate}</div>`
      );
      fs.mkdirSync(`oca-comp-parsed/${directoryPath}/${dir}/parsed/${comp}/`, {
        recursive: true,
      });
      fs.writeFileSync(`oca-comp-parsed/${directoryPath}/${dir}/parsed/${comp}/${comp}.html`, file, "utf8");
      
      createIndexPageData(directoryPath, dir, comp);
    }

    const createIndexPageData = (directoryPath, dir, comp) => {
      
      indexObj[directoryPath][dir].push(
        {
          path: `${directoryPath}/${dir}/parsed/${comp}/${comp}.html`,
          name: comp
        }
      )
    }

    const createIndexPage = () => {
      let template = fs.readFileSync(`src/main/webpack/commons/fe-hbs-templates/component-index.handlebars`, 'utf8');
      let compiledTemplate = Handlebars.compile(template);
      let parsedTemplate = compiledTemplate(indexObj);
      fs.writeFileSync(`oca-comp-parsed/index.html`, parsedTemplate, "utf8");
    }

    handlebarParse('components');
    //   handlebarParse('layouts');
    createIndexPage();

  } catch (err) {
    console.log(err)
  }
}


module.exports = (env) => {
  return handlebarParser(env);
}