// dependencies
const $ = require('jquery')
const tmi = require('tmi.js')
const fs = require('fs')
const path = require('path')
const buffer = require('buffer')

const filepath = path.join(__dirname, '../data/bttv-raw.txt')

fs.readFile(filepath, 'utf8' , (err, data) => {
  if (err) {
    console.error(err)
    return
  }

  const matches = [...data.matchAll(/<a.*?<img.*?src=\"(.*?)\".*?<\/a>/g)]
    .map(arr => arr.slice(0, 2))
    .map(([all, src]) => [all.match(/<div.*?>(.*?)<\/div>/)[1], src])

   const stream = fs.createWriteStream(path.join(__dirname, '../data/emotes.csv'))
   stream.write('name, src\n')
   matches.forEach(([name, src]) => {
     stream.write(`${name}, ${src} \n`)
   })
   stream.end()

})
