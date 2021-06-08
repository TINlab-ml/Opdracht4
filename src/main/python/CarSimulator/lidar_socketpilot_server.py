# ====== Legal notices
#
# Copyright (C) 2013 - 2020 GEATEC engineering
#
# This program is free software.
# You can use, redistribute and/or modify it, but only under the terms stated in the QQuickLicence.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY, without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
# See the QQuickLicence for details.
#
# The QQuickLicense can be accessed at: http://www.geatec.com/qqLicence.html
#
# __________________________________________________________________________
#
#
#  THIS PROGRAM IS FUNDAMENTALLY UNSUITABLE FOR CONTROLLING REAL SYSTEMS !!
#
# __________________________________________________________________________
#
# It is meant for training purposes only.
#
# Removing this header ends your licence.
#

import socket as sc
import time as tm
import json as js
import simpylc as sp
import sys

this = sys.modules[__name__]

this.port = 50012

def setPort(port):
    this.port = int(port)

class LidarSocketpilotServer:
    host = 'localhost'
    socketType = sc.AF_INET, sc.SOCK_STREAM
    maxNrOfConnectionRequests = 5
    maxMessageLength = 2048

    def __init__ (self):
        with sc.socket (*self.socketType) as serverSocket:
            serverSocket.bind ((self.host, this.port))
            serverSocket.listen (self.maxNrOfConnectionRequests)

            while True:
                self.clientSocket, address = serverSocket.accept ()

                with self.clientSocket:
                    while True:
                        sensors = {
                            'lidarDistances': sp.world.visualisation.lidar.distances,
                            'lidarHalfApertureAngle': sp.world.visualisation.lidar.halfApertureAngle,
                            'isOnTrack': sp.world.visualisation.isOnTrack(),
                            'progress': "{:.1f}".format(sp.world.visualisation.getProgress()),
                            'lapTime': "{:.2f}".format(sp.world.visualisation.getLapTime()),
                            'collided': sp.world.visualisation.floor.scene.collided
                        }
                        self.send (sensors)

                        tm.sleep (0.02)

                        actuators = self.recv ()
                        sp.world.physics.steeringAngle.set (actuators ['steeringAngle'])
                        sp.world.physics.targetVelocity.set (actuators ['targetVelocity'])

    def send (self, sensors):
        buffer = bytes (f'{js.dumps (sensors):<{self.maxMessageLength}}', 'ascii')

        totalNrOfSentBytes = 0

        while totalNrOfSentBytes < self.maxMessageLength:
            nrOfSentBytes = self.clientSocket.send (buffer [totalNrOfSentBytes:])

            if not nrOfSentBytes:
                self.raiseConnectionError ()
                
            totalNrOfSentBytes += nrOfSentBytes

    def recv (self):
        totalNrOfReceivedBytes = 0
        receivedChunks = []

        while totalNrOfReceivedBytes < self.maxMessageLength:
            receivedChunk = self.clientSocket.recv (self.maxMessageLength - totalNrOfReceivedBytes)

            if not receivedChunk:
                self.raiseConnectionError ()

            receivedChunks.append (receivedChunk)
            totalNrOfReceivedBytes += len (receivedChunk)
            
        return js.loads (b''.join (receivedChunks) .decode ('ascii'))

    def raiseConnectionError (self):
        raise RuntimeError ('Socket connection broken')
    