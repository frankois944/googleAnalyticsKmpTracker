const path = require("path");
const os = require("os");

// Adapted from: https://github.com/ryanclark/karma-webpack/issues/498#issuecomment-790040818
const output = {
  path: path.join(os.tmpdir(), '_karma_webpack_') + Math.floor(Math.random() * 1000000),
}
config.set({
  webpack: {...config.webpack, output},
client: {
      mocha: {
          timeout: 90000
      }
  }
});
config.files.push({
  pattern: `${output.path}/**/*`,
  watched: false,
  included: false,
});
