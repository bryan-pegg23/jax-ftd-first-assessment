import fs from 'fs'
import net from 'net'
import vorpal from 'vorpal'
import { hash, compare } from './hashing'

const cli = vorpal()

cli
  .delimiter('Arth$')

cli
  .command('register <username> <password>')
  .description('Registers user on server')
  .action(function (args, callback) {
    let server = net.createConnection({port: 667}, () => {
      let command = 'register'
      let hashed = hash(args.password)
      let hashedTo
      hashed.then((hash) => hashedTo = hash)
      const data = JSON.stringify(`ClientMessage: {${command}, content: ${args.username} ${hashed}}`)
      fs.writeFile('output.json', data)
      server.write(data)
      this.log('Wrote to server')
      server.on('data', (data) => {
        const { serverResponse } = JSON.parse(data.toString())
        if (serverResponse.error) {
          this.log(`${serverResponse.message}`)
          callback()
        } else {
          this.log(`${serverResponse.data}`)
          callback()
        }
      })
    })
  })



export default cli
