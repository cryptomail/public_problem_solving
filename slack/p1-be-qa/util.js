module.exports = function(secretToken) {
  return {
  	secretToken: secretToken,
  	
  	errorlog: function() {
      var d = new Date();
      console.log(d, '*****ERROR' + lineNumberAndFile(), arguments)
    },
    log: function() {
      var d = new Date();

      console.log(d, '*****DEBUG' + lineNumberAndFile(), arguments)
    }
  };
};