import fs from 'fs'
import net from 'net'
import vorpal from 'vorpal'
import { hash, compare } from './hashing'

const cli = vorpal()

let server

const closeConnection = () => {
  server.end()
}


cli
  .delimiter('Arth$')

cli
  .command('register <username> <password>')
  .description('Registers user on server')
  .action(function (args, callback) {
   server = net.createConnection({port: 667}, () => {
      let command = 'register'
      let hash2
      let a = hash(args.password)
      a.then(function (hashedPassword){
        (fs.writeFile('output.json', JSON.stringify(`{clientMessage: {command: ${command}, content: ${args.username} ${hashedPassword}}}`) + '\n'))
        server.write(JSON.stringify({clientMessage: {command: command, content: args.username + hashedPassword}}) + '\n')
      })
      .catch((err) => cli.log(err))
    //  server.write(data)
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
