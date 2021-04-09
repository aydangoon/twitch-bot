// dependencies
const $ = require('jquery')
const tmi = require('tmi.js')
const fs = require('fs')
const path = require('path')

// constants
const CLIENT_ID = '43iyp2tplckbrvr6abdnfzbop7xno1'

// command line args
const args = process.argv.slice(2)

if (args.includes('-h')) {
  console.log(`twitch-bot chat message crawler. A little twitch spider creeper sneaky goblin, if you will.
  -c <channel name>: Specify the channel to collect messages on. If no channel is given, defaults to xqc's channel.
  -overwrite: If this flag is included this script will overwrite the target file.
  -t <file name>: Specify the target file. If this argument is not passed then defaults to ../data/raw.csv
  `)
  return 0
}

const overwrite = args.includes('-overwrite')
const channel = (args.includes('-c') && args.indexOf('-c') + 1 !== args.length) ? args[args.indexOf('-c') + 1] : 'xqcow'
const filepath = path.join(
  __dirname,
  (args.includes('-t') && args.indexOf('-t') + 1 !== args.length) ? args[args.indexOf('-t') + 1] : '../data/raw.csv'
)

fs.access(filepath, fs.constants.F_OK | fs.constants.W_OK, (err) => {
  if (err) {
    console.error('File does not exist or is read-only.')
    return 1
  }

  const stream = fs.createWriteStream(filepath, { flags: overwrite ? 'w' : 'a' })
  process.on('SIGINT', () => {
    stream.end()
    console.log('\nstream closed, cleanup good!')
    process.exit()
  });

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

  let msgNum = 1
  client.on('message', (channel, tags, message) => {

    // destructure
    const { subscriber, badges, mod } = tags

    // get data
    const sub = subscriber ? tags['badge-info']['subscriber'] : 0
    const bits = (badges && badges['bits']) || 0
    const isMod = mod ? 1 : 0
    const username = tags['display-name']
    const bot = (/bot/i).test(username) ? 1 : 0
    const msg = `"${message.replace(/"/g, "\"\"")}"`

    stream.write(`${username}, ${isMod}, ${sub}, ${bot}, ${bits}, ${msg}\n`, () => {
      console.log('stored message', msgNum++)
    })

  });

})
