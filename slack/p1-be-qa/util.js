
function getErrorObject(){
    try { throw Error('') } catch(err) { return err; }
}

function lineNumberAndFile() {
  var err = getErrorObject();
  var caller_line = err.stack.split("\n")[5];
  var index = caller_line.indexOf("at ");
  var clean = caller_line.slice(index+2, caller_line.length);

  return caller_line;
}

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