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
  \file texture.cpp
  \brief Texture
  \author Jens Thiele
*/

//#include "typedefs.h"
#include "texture.h"
#include <SDL_image.h>

Texture::Texture(int size)
{
  width=height=size;
  
  glGenTextures(1,&textureID);
  glBindTexture(GL_TEXTURE_2D, textureID);                    
    
  // we do not upload any texture => pixels is NULL
  // => we can specify any format
  // the internal format has double usage:
  // old usage: specify number of color components (since we do not upload a texture this is of no use to us)
  // second: it is a hint to the driver
  // see also: http://www.opengl.org/developers/documentation/Specs/glspec1.1/node79.html#SECTION00680100000000000000
  //  glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB5, size, size, 0, GL_RGB, GL_UNSIGNED_SHORT, NULL);
  glTexImage2D(GL_TEXTURE_2D, 0, GL_R3_G3_B2, size, size, 0, GL_RGB, GL_UNSIGNED_SHORT, NULL);
    
  // Set the texture quality
  glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
  glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);

  GL_ERRORS();
}

Texture::Texture(unsigned dsize, const char* data, const char *extension, const char *mimeType)
{
  SDL_PixelFormat RGB_PixelFormat = {
    NULL,
    18,3,
    0,0,0,8,
    0,8,10,0,
    0xff,0xff00,0xff0000,0x0,
    0,
    0xff
  };
    
  SDL_PixelFormat RGBA_PixelFormat = {
    NULL,
    32,4,
    0,0,0,0,
    0,8,16,24,
    0xff,0xff00,0xff0000,0xff000000,
    0,
    0xff
  };

  SDL_Surface* image=loadImage(dsize, data, extension, mimeType);
  GLint internalformat;
  GLint format;
  GLenum type=GL_UNSIGNED_BYTE;
  SDL_PixelFormat* pixelformat;
  if (image->format->Amask) {
    // We have got a alpha channel !
    internalformat=GL_RGBA8;
    format=GL_RGBA;
    haveAlphaChannel=true;
    pixelformat=&RGBA_PixelFormat;
  }else{
    // as fallback convert to rgb
    internalformat=GL_RGB8;
    format=GL_RGB;
    haveAlphaChannel=false;
    pixelformat=&RGB_PixelFormat;
  }
  SDL_Surface* tmp=SDL_ConvertSurface(image,pixelformat,SDL_SWSURFACE);
  SDL_FreeSurface(image);
  image=tmp;
  tmp=NULL;
  width=image->w;
  height=image->h;
  if (haveAlphaChannel&&(0<0)) {
    // set alpha channel to on or off
    // I thought mesa would be much faster in this case but it isn't
    // => this is nearly dead code
    SDL_LockSurface(image);
    
    SDL_PixelFormat *fmt;
    Uint32 temp, pixel;
    Uint8 alpha;
    fmt=image->format;
    DOPE_CHECK(fmt->BytesPerPixel==4);
    DOPE_CHECK(!fmt->Aloss);
    DOPE_CHECK(fmt->Amask>>fmt->Ashift==255);
    // this is really slow - but it is not called often and
    // easy to understand
    for (int c=0;c<width*height;++c) {
      pixel=((Uint32*)image->pixels)[c];
      /* Get Alpha component */
      temp=pixel&fmt->Amask; /* Isolate alpha component */
      temp=temp>>fmt->Ashift;/* Shift it down to 8-bit */
      temp=temp<<fmt->Aloss; /* Expand to a full 8-bit number */
      alpha=(Uint8)temp;
      alpha=(alpha>100) ? 255 : 0;
      temp=alpha;

      
      temp=temp>>fmt->Aloss;
      temp=temp<<fmt->Ashift;
      pixel&=~fmt->Amask;
      pixel|=(temp&fmt->Amask);
      ((Uint32*)image->pixels)[c]=pixel;
    }
    SDL_UnlockSurface(image);
  }
  
  glGenTextures(1,&textureID);
  glBindTexture(GL_TEXTURE_2D,textureID);
  int q=(2>1) ? GL_LINEAR : GL_NEAREST;
  glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,q); 
  glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,q);
  // 2d texture, level of detail 0 (normal), 3 components (red, green, blue), x size from image, y size from image,
  // border 0 (normal), rgb color data, unsigned byte data, and finally the data itself.
  glTexImage2D(GL_TEXTURE_2D, 0, internalformat, width, height, 0, format , type, image->pixels);
  SDL_FreeSurface(image);
}

Texture::~Texture()
{
  glDeleteTextures(1,&textureID);
  DOPE_MSG("Info:","freed texture");
}

SDL_Surface*
Texture::loadImage(unsigned dsize, const char* data, const char *extension, const char *mimeType)
{
  SDL_RWops* rw = SDL_RWFromMem((void *)data,dsize);
  SDL_Surface* s=IMG_Load_RW(rw,true);
  if (!s) DOPE_FATAL("Could not load image");
  return s;
};

void 
Texture::copyFromScreen(int swidth, int sheight)
{
  glBindTexture(GL_TEXTURE_2D,textureID);
  glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 0, 0, swidth, sheight);
  GL_ERRORS();
}

