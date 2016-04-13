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
1. ~~Simple positive test ~~04/12/2016
2. Negative files.upload
  * ~~Invalid auth~~04/13/2016
  * ~~No auth~~ 04/13/2016
  * ~~No content~~ 04/13/2016

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

####Files.list
1. Simple positive test
2. Negative files.upload
3. Super evil tests

###Files.delete
1. Simple positive test
2. Negative files.upload
3. Super evil tests