// dependencies
const tmi = require('tmi.js')
const fs = require('fs')
const path = require('path')
const buffer = require('buffer')
const https = require('https')

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
   stream.write('name, ext, src\n')
   matches.forEach(([name, src]) => {
     https.get(src, (res) => {
       stream.write(`${name}, ${res.headers['content-type'].slice(6)}, ${src}\n`)
     })
   })
})
