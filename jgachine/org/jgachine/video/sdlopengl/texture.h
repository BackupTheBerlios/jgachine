/*
 * Copyright (C) 2002 Jens Thiele <karme@berlios.de>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

/*!
   \file texture.h<2>
   \brief texture class
   \author Jens Thiele
   \note should be called GLTexture
*/


#ifndef TEXTURE_H
#define TEXTURE_H

#include <SDL.h>
#include "sdlgl.h"

//! class representing a OpenGL texture
class Texture
{
public:
  //! create empty texture
  Texture(int size);
  
  //! create texture from image
  Texture(unsigned dsize, const char* data, const char *extension=NULL, const char *mimeType=NULL);
  ~Texture();

  bool isTransparent() const {return haveAlphaChannel;}
  int getWidth() const {return width;}
  int getHeight() const {return height;}
  GLuint getTextureID() const {return textureID;}
  //! copy screen to texture
  void copyFromScreen(int swidth, int sheight);
  
protected:
  GLuint textureID;
  int width;
  int height;
  bool haveAlphaChannel;

  static SDL_Surface* loadImage(unsigned dsize, const char* data, const char *extension=NULL, const char *mimeType=NULL);
};

#endif
