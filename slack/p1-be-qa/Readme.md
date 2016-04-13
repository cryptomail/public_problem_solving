#Slack API Testing


##This test library requires Mocha.  Mocha installation is very easy and can be found here: https://mochajs.org/#installation

###For the impatient, download source into current working dir:
```bash
$ npm install -g mocha
$ npm install
$ mocha
```

####Todo List:

####Files.Upload
1. Positive tests 
  ** ~~Simple positive test~~ 04/12/2016 
  ** ~~Simple unknown field in payload test: muppets in form!~~ 04/12/2016 
  ** ~~unknown content resolves to correct content~~ 04/13/2016 Real PDF upload respresented as kermitthefrog content type.
  ** ~~unset file type resolves to correct content~~ 04/13/2016 
2. Negative files.upload
  * ~~Invalid auth~~04/13/2016
  * ~~No auth provided~~ 04/13/2016
  * ~~No content, empty file~~ 04/13/2016 NOT DOCUMENTED ON SLACK!!! Filed under "OTHER" in documentation easy out.
  * ~~misnomed content~~ 04/13/2016 Real PDF upload respresented as PNG
  * ~~send to invalid channel~~ 04/13/2016 

3. Super evil tests
  * unknown entity fields: Determination:  Slack API is friendly in that ignores unknown elements: 
  ```javascript
  	var formData = {
							muppets: 'animal, kermit, miss piggy',
							file: {
								value:  fs.createReadStream(fnamePath),
								options: {
									filename: fname,
									contentType: 'image/png'
								}
							}	
						};
```
  * bust a field limit
  * misnomed file... put a PDF into a PNG
  * channels with all kinds of crap in it 1,000 commas
  * channels: 1 MB channel name followed by a single channel
  * channels: same channel twice

####Files.list
1. Simple positive test
2. Negative files.upload
3. Super evil tests

###Files.delete
1. Simple positive test
2. Negative files.upload
3. Super evil tests