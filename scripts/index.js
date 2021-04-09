// dependencies
const $ = require('jquery')
const tmi = require('tmi.js')
const fs = require('fs')
const path = require('path')
const buffer = require('buffer')

// constants
const CLIENT_ID = '43iyp2tplckbrvr6abdnfzbop7xno1'


// command line args
const args = process.argv.slice(2)

if (args.includes('-h')) {
  console.log(`twitch-bot chat message crawler. A little twitch spider creeper sneaky goblin, if you will.
  -c <channel name>: Specify the channel to collect messages on. If no channel is given, defaults to xqc's channel.
  -overwrite: If this flag is included this script will overwrite the target file.
  -t <file name>: Specify the target file. If this argument is not passed then defaults to ../data/raw.csv
  -size <size>: Limit the total amount of data to be collected to <size> megabytes. Defaults to 1GB.
  `)
  return 0
}

const overwrite = args.includes('-overwrite')
const channel = (args.includes('-c') && args.indexOf('-c') + 1 !== args.length) ? args[args.indexOf('-c') + 1] : 'xqcow'
const filepath = path.join(
  __dirname,
  (args.includes('-t') && args.indexOf('-t') + 1 !== args.length) ? args[args.indexOf('-t') + 1] : '../data/raw.csv'
)
const sizeLimit = (args.includes('-size') && args.indexOf('-size') + 1 !== args.length)
  ? parseInt(args[args.indexOf('-size') + 1])
  : 1000

fs.access(filepath, fs.constants.F_OK | fs.constants.W_OK, (err) => {
  if (err) {
    console.error('File does not exist or is read-only.')
    return 1
  }

  const stream = fs.createWriteStream(filepath, { flags: overwrite ? 'w' : 'a' })

  const endSafely = () => {
    stream.end()
    console.log('\nstream closed, cleanup good!')
    process.exit()
  }

  process.on('SIGINT', endSafely)

  if (overwrite) {
    stream.write('username, mod, sub, bot, bits, message\n')
  }

  const client = new tmi.Client({
    connection: { reconnect: true },
    channels: [channel],
    id: CLIENT_ID,
    secret: process.env.CLI_SECRET || ' '
  })

  client.connect()

  let dataCollected = 0
  let messageCount = 0
  let rate = 'N/A'
  var messagesLastSecond = 0
  setInterval(() => {
    rate = messagesLastSecond
    messagesLastSecond = 0
  }, 1000)

  client.on('message', (channel, tags, message) => {

    if (dataCollected >= sizeLimit) {
      console.log('Data limit reached, ending farming...')
      endSafely()
    }

    // destructure
    const { subscriber, badges, mod } = tags

    // get data
    const sub = subscriber ? tags['badge-info']['subscriber'] : 0
    const bits = (badges && badges['bits']) || 0
    const isMod = mod ? 1 : 0
    const username = tags['display-name']
    const bot = (/bot/i).test(username) ? 1 : 0
    const msg = `"${message.replace(/"/g, "\"\"")}"`

    const line = `${username}, ${isMod}, ${sub}, ${bot}, ${bits}, ${msg}\n`
    stream.write(line, () => {
      const mbsAdded = Buffer.byteLength(line, 'utf8') / 1000000
      dataCollected += mbsAdded
      messagesLastSecond++
      console.clear()
      console.log('=================== process info ====================')
      console.log('total messages farmed:', ++messageCount)
      console.log('messages / second:', rate)
      console.log('MBs farmed:', Math.ceil(dataCollected * 1000) / 1000, 'out of', sizeLimit)
      console.log('last message:', msg)
    })

  });

})
