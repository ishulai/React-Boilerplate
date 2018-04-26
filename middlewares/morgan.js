// Event logger

const fs = require("fs");
const morgan = require("morgan");

module.exports = (app, appEnv) => {
  app.use(morgan("combined", {
    stream: fs.createWriteStream(appEnv.logs.access, {flags: "a"})
  }));
}