// Express "modules" - runs during Express requests

module.exports = (app, appEnv) => {
  require("./passport")(app, appEnv);
  require("./sass")(app, appEnv);
  require("./session")(app, appEnv);
  require("./webpack")(app, appEnv);

  if(appEnv.development) {
    require("./morgan")(app, appEnv);
  }
}