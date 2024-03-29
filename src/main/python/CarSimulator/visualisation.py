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
# The QQuickLicense can be accessed at: http://www.qquick.org/license.html
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

'''

      z
      |
      o -- y
     /
    x

'''

import random as rd

import simpylc as sp

import parameters as pm

import numpy as np

from shapely.geometry import Point, Polygon

normalFloorColor = (0, 0.003, 0)
collisionFloorColor = (1, 0, 0.3)
nrOfObstacles = 64

class Lidar:
    # 0, ...,  halfApertureAngle - 1, -halfApertureAngle, ..., -1
    
    def __init__ (self, apertureAngle, obstacles):
        self.apertureAngle = apertureAngle
        self.halfApertureAngle = self.apertureAngle // 2
        self.obstacles = obstacles
        self.distances = [sp.finity for angle in range (self.apertureAngle)]
        
    def scan (self, mountPosition, mountAngle):
        self.distances = [sp.finity for angle in range (self.apertureAngle)]
        
        for obstacle in self.obstacles:
            relativePosition = sp.tSub (obstacle.center, mountPosition) 
            distance = sp.tNor (relativePosition)
            absoluteAngle = sp.atan2 (relativePosition [1], relativePosition [0])
            relativeAngle = (round (absoluteAngle - mountAngle) + 180) % 360 - 180 
                
            if -self.halfApertureAngle <= relativeAngle < self.halfApertureAngle - 1:
                self.distances [relativeAngle] = round (min (distance, self.distances [relativeAngle]), 4)    # In case of coincidence, favor nearby obstacle

class Line (sp.Cylinder):
    def __init__ (self, **arguments):
       super () .__init__ (size = (0.01, 0.01, 0), axis = (1, 0, 0), angle = 90, color = (0, 1, 1), **arguments)

class BodyPart (sp.Beam):
    def __init__ (self, **arguments):
        super () .__init__ (color = (1, 0, 0), **arguments)

class Wheel:
    def __init__ (self, **arguments): 
        self.suspension = sp.Cylinder (size = (0.01, 0.01, 0.001), axis = (1, 0, 0), angle = 90, pivot = (0, 0, 1), **arguments)
        self.rim = sp.Beam (size = (0.08, 0.06, 0.02), pivot = (0, 1, 0), color = (0, 0, 0))
        self.tire = sp.Cylinder (size = (pm.wheelDiameter, pm.wheelDiameter, 0.04), axis = (1, 0, 0), angle = 90, color = (1, 1, 0))
        self.line = Line ()
        
    def __call__ (self, wheelAngle, slipping, steeringAngle = 0):
        return self.suspension (rotation = steeringAngle, parts = lambda:
            self.rim (rotation = wheelAngle, parts = lambda:
                self.tire (color = (rd.random (), rd.random (), rd.random ()) if slipping else (1, 1, 0)) +
                self.line ()
        )   )
        
class Window (sp.Beam):
    def __init__ (self, **arguments):
        super () .__init__ (axis = (0, 1, 0), color = (0, 0, 1), **arguments)
        
class Floor (sp.Beam):
    side = 16
    spacing = .5
    halfSteps = round (0.5 * side / spacing)

    class Stripe (sp.Beam):
        def __init__ (self, **arguments):
            super () .__init__ (size = (0.01, Floor.side, 0.001), **arguments)
            
    def __init__ (self, **arguments):
        super () .__init__ (size = (self.side, self.side, 0.0005), color = normalFloorColor)
        self.xStripes = [self.Stripe (center = (0, nr * self.spacing, 0.0001), angle = 90, color = (1, 1, 1)) for nr in range (-self.halfSteps, self.halfSteps)]
        self.yStripes = [self.Stripe (center = (nr * self.spacing, 0, 0), color = (0, 0, 0)) for nr in range (-self.halfSteps, self.halfSteps)]
        
    def __call__ (self, parts):
        return super () .__call__ (color = collisionFloorColor if self.scene.collided else  normalFloorColor, parts = lambda:
            parts () +
            sum (xStripe () for xStripe in self.xStripes) +
            sum (yStripe () for yStripe in self.yStripes)
        )

class Visualisation (sp.Scene):
    def __init__ (self):
        super () .__init__ ()
        
        self.camera = sp.Camera ()
        
        self.floor = Floor (scene = self)
        
        self.fuselage = BodyPart (size = (0.70, 0.16, 0.08), center = (0, 0, 0.07), pivot = (0, 0, 1), group = 0)
        self.fuselageLine = Line ()
        self.cabin = BodyPart (size = (0.20, 0.16, 0.06), center = (-0.06, 0, 0.07))
        
        self.wheelFrontLeft = Wheel (center = (pm.wheelShift, 0.08, -0.02))
        self.wheelFrontRight = Wheel (center = (pm.wheelShift, -0.08, -0.02))
        
        self.wheelRearLeft = Wheel (center = (-pm.wheelShift, 0.08, -0.02))
        self.wheelRearRight = Wheel (center = (-pm.wheelShift, -0.08, -0.02))
        
        self.windowFront = Window (size = (0.05, 0.14, 0.14), center = (0.14, 0, -0.025), angle = -60)    
        self.windowRear = Window (size = (0.05, 0.14, 0.18), center = (-0.18, 0, -0.025),angle = 72) 

        self.viewBoxes = []

        if False:
            helfAperture = 60
            numOfBoxes = 6
            anglePerBox = int((helfAperture*2)/numOfBoxes)
            for angle in range(-helfAperture, helfAperture+1, anglePerBox):
                self.viewBoxes.append(BodyPart (size = (8, 0.01, 0.01), center =  (0, 0, 0.07), angle=angle))



        self.roadCones = []

        self.leftConesPos = np.array(((3.0, -5.5),(4.5, -6.0),(6.0, -5.5),(6.0, -4.0),(6.0, -2.5),(5.5, -0.5),(5.0, 1.5),(5.0, 3.5),(5.5, 5.5),(5.0, 6.5),(4.0, 6.5),(2.5, 6.0),(2.0, 5.0),(1.5, 3.5),(1.5, 1.5),(1.5, -0.5),(0.5, -2.5),(-1.5, -3.5),(-3.0, -3.5),(-4.5, -2.5),(-4.5, -0.5),(-4.0, 1.0),(-3.5, 2.5),(-3.5, 4.0),(-3.5, 5.5),(-4.0, 6.0),(-5.0, 6.0),(-6.0, 5.5),(-6.5, 4.5),(-6.0, 3.0),(-6.5, 1.0),(-6.5, -1.0),(-6.0, -2.5),(-6.5, -4.5),(-5.5, -6.0),(-4.5, -6.5),(-3.0, -6.5),(-1.5, -6.0),(0.5, -5.5),))
        self.rightConesPos = np.array(((3.0, -6.5),(5.0, -7.0),(7.0, -6.5),(7.0, -4.5),(7.0, -2.5),(6.5, -0.5),(6.0, 1.5),(6.0, 3.5),(6.5, 5.5),(6.0, 7.0),(4.0, 7.5),(2.0, 7.0),(1.0, 5.5),(0.5, 3.5),(0.5, 1.5),(0.5, -0.5),(-0.5, -2.0),(-2.0, -2.5),(-3.5, -2.0),(-3.5, -0.5),(-3.0, 1.0),(-2.5, 2.5),(-2.5, 4.0),(-2.5, 6.0),(-3.5, 7.0),(-5.0, 7.0),(-7.0, 6.5),(-7.5, 5.0),(-7.0, 3.0),(-7.5, 1.0),(-7.5, -1.0),(-7.0, -2.5),(-7.5, -4.5),(-6.5, -6.5),(-4.5, -7.5),(-2.5, -7.5),(-1.0, -7.0),(1.0, -6.5),))

        self.circuitgon = Polygon(np.concatenate((self.leftConesPos, (self.leftConesPos[0],), self.rightConesPos, (self.rightConesPos[0],))))

        self.progress = 0
        self.lastCone = 0

        track = open ('./assets/tracklayouts/default.track')
        
        for rowIndex, row in enumerate (track):
            for columnIndex, column in enumerate (row):
                if column == '*':
                    self.roadCones.append (sp.Cone (
                        size = (0.07, 0.07, 0.15),
                        center = (columnIndex / 4 - 8, rowIndex / 2 - 8, 0.15),
                        color = (1, 0.3, 0),
                        group = 1
                    ))
                elif column == "@":
                    self.startX = columnIndex / 4 - 8
                    self.startY = rowIndex / 2 - 8
                    self.init = True
                    
        track.close ()
        
        self.lidar = Lidar (120, self.roadCones)
        
    def display (self):
        if self.init:
            self.init = False
            sp.world.physics.positionX.set (self.startX) 
            sp.world.physics.positionY.set (self.startY)
        
        '''
        self.camera (   # First person
            position = sp.tEva ((sp.world.physics.positionX, sp.world.physics.positionY, 1)),
            focus = sp.tEva ((sp.world.physics.focusX, sp.world.physics.focusY, 0))
        )
        
        '''
        self.camera (   # Soccer match
            position = sp.tEva ((sp.world.physics.positionX + 2, sp.world.physics.positionY, 9)),
            focus = sp.tEva ((sp.world.physics.positionX + 0.001, sp.world.physics.positionY, 0))
        )

        '''
        self.camera (   # Helicopter
            position = sp.tEva ((0.0000001, 0, 20)),
            focus = sp.tEva ((0, 0, 0))
        )
        '''
        
        self.floor (parts = lambda:
            self.fuselage (position = (sp.world.physics.positionX, sp.world.physics.positionY, 0), rotation = sp.world.physics.attitudeAngle, parts = lambda:
                self.cabin (parts = lambda:
                    self.windowFront () +
                    self.windowRear () +
                    sum(box() for box in self.viewBoxes)
                ) +
                
                self.wheelFrontLeft (
                    wheelAngle = sp.world.physics.midWheelAngle,
                    slipping = sp.world.physics.slipping,
                    steeringAngle = sp.world.physics.steeringAngle
                ) +
                self.wheelFrontRight (
                    wheelAngle = sp.world.physics.midWheelAngle,
                    slipping = sp.world.physics.slipping,
                    steeringAngle = sp.world.physics.steeringAngle
                ) +
                
                self.wheelRearLeft (
                    wheelAngle = sp.world.physics.midWheelAngle,
                    slipping = sp.world.physics.slipping
                ) +
                self.wheelRearRight (
                    wheelAngle = sp.world.physics.midWheelAngle,
                    slipping = sp.world.physics.slipping
                ) +
                
                self.fuselageLine ()
            ) +
            
            sum (roadCone () for roadCone in self.roadCones)
        )
                
        try:
            self.lidar.scan (self.fuselage.position, self.fuselage.rotation)
        except Exception as exception: # Initial check
            pass
            # print ('Visualisation.display:', exception)
        

    def getPosition(self):
        return Point(sp.world.physics.positionX+0, sp.world.physics.positionY+0)

    def isOnTrack(self):
        return self.getPosition().within(self.circuitgon)

    def getProgress(self):

        posPoint = self.getPosition()
        posArray = (posPoint.x, posPoint.y)

        distances = np.sum((self.leftConesPos-posArray)**2, axis=1)
        nearestCone = np.argmin(distances)
        numOfInnerCones = len(self.leftConesPos)

        newConeDiff = 1 if (self.lastCone == numOfInnerCones-1 and nearestCone == 0) else (nearestCone - self.lastCone)

        if newConeDiff == 1 or newConeDiff == -1:
            self.progress += (newConeDiff / numOfInnerCones) * 100
            self.lastCone = nearestCone

        if self.progress >= 100:
            self.progress = 0
            return 100
        else:
            return self.progress

    def getLapTime(self):
        return sp.world.time()
        
