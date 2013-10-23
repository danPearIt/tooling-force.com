/*
 * Copyright (c) 2013 Andrey Gavrikov.
 * this file is part of tooling-force.com application
 * https://github.com/neowit/tooling-force.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.neowit.apex.tooling



object Runner extends Logging {
    val appConfig = Config.getConfig

    def main(args: Array[String]) {
        if (args.isEmpty) {
            appConfig.help()

        } else {
            try {
                appConfig.load(args.toList)
                run()
            } catch {
                case ex: InvalidCommandLineException => appConfig.help()
                case ex: MissingRequiredConfigParameterException => logger.error(ex.getMessage)
                case ex: Throwable =>
                    //val response = appConfig.responseWriter with Response
                    object response extends Response
                    response.genericError(ex.getMessage)

            } finally {
                appConfig.responseWriterClose
            }
        }
    }

    def run () {

        val session = new SfdcSession(appConfig)
        val sessionData = new SessionData(appConfig, session)
        sessionData.load()

        val handler = ActionHandler.getHandler(appConfig)
        handler.act(session, sessionData)
        session.storeSessionData()

        //Tester.runTest(session)

    }


}