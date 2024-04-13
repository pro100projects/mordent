import React from 'react';
import Box from '@mui/material/Box';
import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';
import FavoriteIcon from '@mui/icons-material/Favorite';
import { Grid, IconButton, Slider } from '@mui/material';
import { Stack, styled } from '@mui/system';
import Typography from '@mui/material/Typography';
import {
  FastForwardRounded,
  FastRewindRounded,
  PauseRounded,
  PlayArrowRounded
} from '@mui/icons-material';
import { getDurationFromSeconds } from '../../shared/data-mapping';
import useAudioPlayer from './use-audio-player.component';
import mockSongImage from './../../images/mock-music-image.jpeg';
import ShuffleIcon from '@mui/icons-material/Shuffle';
import RepeatIcon from '@mui/icons-material/Repeat';
import RepeatOneIcon from '@mui/icons-material/RepeatOne';
import VolumeOffRoundedIcon from '@mui/icons-material/VolumeOffRounded';
import VolumeMuteRoundedIcon from '@mui/icons-material/VolumeMuteRounded';
import VolumeDownRoundedIcon from '@mui/icons-material/VolumeDownRounded';
import VolumeUpRoundedIcon from '@mui/icons-material/VolumeUpRounded';

import './audio-player.component.scss';

const WallPaper = styled('div')({
  position: 'absolute',
  top: 0,
  left: 0,
  overflow: 'hidden',
  background: 'linear-gradient(rgb(255, 38, 142) 0%, rgb(255, 105, 79) 100%)',
  transition: 'all 500ms cubic-bezier(0.175, 0.885, 0.32, 1.275) 0s',
  '&:before': {
    content: '""',
    width: '140%',
    height: '140%',
    position: 'absolute',
    top: '-40%',
    right: '-50%',
    background: 'radial-gradient(at center center, rgb(62, 79, 249) 0%, rgba(62, 79, 249, 0) 64%)'
  },
  '&:after': {
    content: '""',
    width: '140%',
    height: '140%',
    position: 'absolute',
    bottom: '-50%',
    left: '-30%',
    background:
      'radial-gradient(at center center, rgb(247, 237, 225) 0%, rgba(247, 237, 225, 0) 70%)',
    transform: 'rotate(30deg)'
  }
});

const Widget = styled('div')({
  padding: 0,
  borderRadius: 16,
  width: 343,
  maxWidth: '100%',
  margin: 'auto',
  position: 'relative',
  zIndex: 1,
  backgroundColor: 'rgba(255,255,255,0.4)',
  backdropFilter: 'blur(40px)'
});

const CoverImage = styled('div')({
  width: 100,
  height: 100,
  objectFit: 'cover',
  overflow: 'hidden',
  flexShrink: 0,
  borderRadius: 8,
  '& > img': {
    width: '100%',
    height: '100%',
    objectFit: 'cover',
    transition: 'opacity 1s ease-in-out',
    opacity: 0
  }
});

const TinyText = styled(Typography)({
  fontSize: '0.75rem',
  opacity: 0.38,
  fontWeight: 500,
  letterSpacing: 0.2
});

const AboutSongWidget = ({ song, handleLike }) => {
  return (
    <Box sx={{ display: 'flex', alignItems: 'center', zIndex: '100' }}>
      <CoverImage>
        {song && (
          <img
            src={
              song.imageFilename
                ? `${origin}/files/images/${song.id}${song.imageFilename}`
                : mockSongImage
            }
            onLoad={(e) => {
              e.target.style.opacity = 1;
            }}
            onError={(e) => {
              e.target.src = mockSongImage;
            }}
          />
        )}
      </CoverImage>
      {song.name && (
        <>
          <Box sx={{ ml: 1.5 }}>
            <Typography fontWeight={500}>{song?.name}</Typography>
            {song?.metadata?.author && song?.metadata?.title && (
              <Typography noWrap>
                {song?.metadata?.author} &mdash; {song?.metadata?.title}
              </Typography>
            )}
          </Box>
          <IconButton sx={{ color: 'red' }} onClick={handleLike}>
            {song.liked ? <FavoriteIcon /> : <FavoriteBorderIcon />}
          </IconButton>
        </>
      )}
    </Box>
  );
};

const PlayAndStopWidget = ({
  audioRef,
  audioLoaded,
  settings,
  handleChangePlay,
  handleMix,
  handlePrev,
  handleNext,
  handleRepeat,
  time,
  handleChangeTime
}) => {
  return (
    <Box>
      <Widget>
        <Box
          sx={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center'
          }}>
          <IconButton aria-label="mix songs" onClick={handleMix}>
            <ShuffleIcon fontSize="small" htmlColor={settings.mix ? '#006cc7' : '#000'} />
          </IconButton>
          <IconButton aria-label="previous song" onClick={handlePrev}>
            <FastRewindRounded fontSize="large" htmlColor={'#000'} />
          </IconButton>
          <IconButton aria-label={settings.play ? 'play' : 'pause'} onClick={handleChangePlay}>
            {settings.play ? (
              <PauseRounded sx={{ fontSize: '3rem' }} htmlColor={'#000'} />
            ) : (
              <PlayArrowRounded sx={{ fontSize: '3rem' }} htmlColor={'#000'} />
            )}
          </IconButton>
          <IconButton aria-label="next song" onClick={handleNext}>
            <FastForwardRounded fontSize="large" htmlColor={'#000'} />
          </IconButton>
          <IconButton aria-label="repeat song" onClick={handleRepeat}>
            {settings.repeatOneSong ? (
              <RepeatOneIcon fontSize="small" htmlColor={'#006cc7'} />
            ) : (
              <RepeatIcon fontSize="small" htmlColor={settings.repeat ? '#006cc7' : '#000'} />
            )}
          </IconButton>
        </Box>
        <Slider
          aria-label="time-indicator"
          size="small"
          value={audioLoaded ? time : 0}
          min={0}
          step={1}
          max={audioLoaded ? audioRef.current.duration : 0}
          onChange={(_, time) => handleChangeTime(time)}
          sx={{
            color: 'rgba(0,0,0,0.87)',
            height: 4,
            '& .MuiSlider-thumb': {
              width: 8,
              height: 8,
              transition: '0.3s cubic-bezier(.47,1.64,.41,.8)',
              '&:before': {
                boxShadow: '0 2px 12px 0 rgba(0,0,0,0.4)'
              },
              '&:hover, &.Mui-focusVisible': {
                boxShadow: '0px 0px 0px 8px rgb(0 0 0 / 16%)'
              },
              '&.Mui-active': {
                width: 20,
                height: 20
              }
            },
            '& .MuiSlider-rail': {
              opacity: 0.28
            }
          }}
        />
        <Box
          sx={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            mt: -2
          }}>
          <TinyText>
            {getDurationFromSeconds(audioLoaded ? audioRef.current.currentTime : 0)}
          </TinyText>
          <TinyText>
            -
            {getDurationFromSeconds(
              audioLoaded ? audioRef.current.duration - audioRef.current.currentTime : 0
            )}
          </TinyText>
        </Box>
      </Widget>
      <WallPaper />
    </Box>
  );
};

const VolumeWidget = ({ volume, handleChangeVolume, handleMuteAndUnmute }) => {
  return (
    <Stack
      spacing={2}
      direction="row"
      sx={{ pr: 3, width: '50%', zIndex: '100' }}
      style={{ zIndex: '100' }}
      alignItems="center">
      <IconButton aria-label="next song" sx={{ m: 0, p: 0 }} onClick={handleMuteAndUnmute}>
        {volume === 0 && <VolumeOffRoundedIcon fontSize="inherit" htmlColor={'#000'} />}
        {volume > 0 && volume <= 100 - (100 / 3) * 2 && (
          <VolumeMuteRoundedIcon fontSize="inherit" htmlColor={'#000'} />
        )}
        {volume > 100 - (100 / 3) * 2 && volume <= 100 - 100 / 3 && (
          <VolumeDownRoundedIcon fontSize="inherit" htmlColor={'#000'} />
        )}
        {volume > 100 - 100 / 3 && <VolumeUpRoundedIcon fontSize="inherit" htmlColor={'#000'} />}
      </IconButton>
      <Slider
        aria-label="Volume"
        defaultValue={volume}
        value={volume}
        onChange={handleChangeVolume}
        sx={{
          color: 'rgba(0,0,0,0.87)',
          '& .MuiSlider-track': {
            border: 'none'
          },
          '& .MuiSlider-thumb': {
            width: 24,
            height: 24,
            backgroundColor: '#fff',
            '&:before': {
              boxShadow: '0 4px 8px rgba(0,0,0,0.4)'
            },
            '&:hover, &.Mui-focusVisible, &.Mui-active': {
              boxShadow: 'none'
            }
          }
        }}
      />
    </Stack>
  );
};

const AudioPlayer = () => {
  const {
    audioRef,
    audioLoaded,
    song,
    settings,
    time,
    volume,
    handleAudioLoaded,
    handleLike,
    handleChangePlay,
    handlePlay,
    handlePause,
    handleEnded,
    handleMix,
    handlePrev,
    handleNext,
    handleRepeat,
    handleChangeTime,
    handleChangeVolume,
    handleMuteAndUnmute
  } = useAudioPlayer();

  return (
    <Box className={'audio-player'}>
      {song && song.id && (
        <audio
          src={song.songFilename && `${origin}/files/songs/${song.id}${song.songFilename}`}
          controls
          ref={audioRef}
          hidden={true}
          onPlay={handlePlay}
          onPause={handlePause}
          onEnded={handleEnded}
          onLoadedData={handleAudioLoaded}
        />
      )}
      <Grid container>
        <Grid item xs={4}>
          <AboutSongWidget song={song} handleLike={handleLike} />
        </Grid>
        <Grid item xs={4}>
          <PlayAndStopWidget
            audioRef={audioRef}
            audioLoaded={audioLoaded}
            settings={settings}
            handleChangePlay={handleChangePlay}
            handleMix={handleMix}
            handlePrev={handlePrev}
            handleNext={handleNext}
            handleRepeat={handleRepeat}
            time={time}
            handleChangeTime={handleChangeTime}
          />
        </Grid>
        <Grid item xs={4} container justifyContent="end" alignItems="center">
          <VolumeWidget
            volume={volume}
            handleChangeVolume={handleChangeVolume}
            handleMuteAndUnmute={handleMuteAndUnmute}
          />
        </Grid>
      </Grid>
    </Box>
  );
};

export default AudioPlayer;
