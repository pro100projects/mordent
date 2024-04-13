import { useEffect, useRef, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  handleNextSongThunk,
  selectPlayerQueue,
  selectPlayerSettings,
  selectPlayerSong,
  setPlayerSetting,
  setPlayerSongParameters,
  setQueue
} from '../../redux/player';
import {
  getSongThunk,
  listenSongThunk,
  mixSongs,
  selectSongs,
  toggleLikeSongThunk
} from '../../redux/song';

const useAudioPlayer = () => {
  const audioRef = useRef();
  const [audioLoaded, setAudioLoaded] = useState(false);
  const [audioPlayerLoaded, setAudioPlayerLoaded] = useState(false);

  const queue = useSelector(selectPlayerQueue);
  const songs = useSelector(selectSongs);
  const song = useSelector(selectPlayerSong);
  const settings = useSelector(selectPlayerSettings);

  const [time, setTime] = useState(settings.time);
  const [listenTime, setListenTime] = useState(0);
  const [volume, setVolume] = useState(settings.volume);

  const dispatch = useDispatch();

  useEffect(() => {
    if (songs && songs.length > 0 && queue && queue.length === 0) {
      dispatch(setQueue(songs.map((song) => song.id)));
      return;
    }
    if (!audioPlayerLoaded) {
      setAudioPlayerLoaded(true);
      const songString = localStorage.getItem('song');
      if (songString) {
        try {
          const song = JSON.parse(songString);
          const currentSongId = queue.find((id) => id === song.id);
          if (currentSongId) {
            dispatch(handleNextSongThunk({ songId: currentSongId }));
            handlePause();
          } else {
            if (song.id) {
              dispatch(getSongThunk({ songId: song.id, play: false }));
            }
            handlePause();
          }
        } catch (error) {
          console.error('Invalid JSON string:', error);
        }
      }
    }
  }, [songs, queue]);

  useEffect(() => {
    if (song && audioLoaded && settings.play) {
      const intervalId = setInterval(() => {
        setTime(audioRef.current.currentTime);
        if (song.listen === false || song.listen === null || song.listen === undefined) {
          setListenTime(listenTime + 1);
          if (
            (audioRef.current.duration >= 120 && listenTime >= 60) ||
            (audioRef.current.duration < 120 &&
              listenTime >= (audioRef.current.duration * 30) / 100)
          ) {
            setListenTime(0);
            dispatch(listenSongThunk(song.id));
            dispatch(setPlayerSongParameters({ listen: true }));
          }
        }
      }, 1000);
      return () => {
        clearInterval(intervalId);
      };
    }
  }, [audioLoaded, song, settings, listenTime]);

  useEffect(() => {
    dispatch(setPlayerSetting({ time: 0 }));
    return () => {
      dispatch(setPlayerSetting({ play: false, time: time, volume: volume }));
    };
  }, []);

  useEffect(() => {
    if (song.id) {
      if (settings.play) {
        audioRef.current.play();
      } else {
        audioRef.current.pause();
      }
    }
  }, [song.id, settings.play]);

  const handleAudioLoaded = () => {
    setAudioLoaded(true);
  };

  const handleLike = () => {
    dispatch(toggleLikeSongThunk(song.id));
  };

  const handleChangePlay = () => {
    if (settings.play) {
      handlePause();
    } else {
      handlePlay();
    }
  };

  const handlePlay = () => {
    if (!song.id) {
      loadPrevAndNextOrRandom();
      return;
    }
    dispatch(setPlayerSetting({ play: true }));
  };

  const handlePause = () => {
    dispatch(setPlayerSetting({ play: false }));
  };

  const handleEnded = () => {
    if (settings.repeat) {
      handleNext();
    } else if (settings.repeatOneSong) {
      handlePlay();
      handleChangeTime(0);
      setListenTime(0);
      dispatch(setPlayerSongParameters({ listen: false }));
    }
  };

  const handlePrev = () => {
    if (settings.repeatOneSong && time > 10) {
      handlePlay();
      handleChangeTime(0);
      setListenTime(0);
      dispatch(setPlayerSongParameters({ listen: false }));
      return;
    }

    let currentSong;
    if (!song.id || !song.prev) {
      currentSong = loadPrevAndNextOrRandom();
      if (currentSong === null) return;
    } else {
      currentSong = song;
    }
    const songIndex = queue.findIndex((id) => id === currentSong.id);
    if (songIndex !== undefined) {
      const prevId = queue[songIndex - 1];
      if (prevId) {
        dispatch(handleNextSongThunk({ songId: prevId }));
      } else {
        dispatch(handleNextSongThunk({ songId: queue[queue.length - 1] }));
      }
    } else {
      dispatch(getSongThunk({ songId: currentSong.prev }));
    }
  };

  const handleNext = () => {
    let currentSong;
    if (!song.id || !song.next) {
      currentSong = loadPrevAndNextOrRandom();
      if (currentSong === null) return;
    } else {
      currentSong = song;
    }
    const songIndex = queue.findIndex((id) => id === currentSong.id);
    if (songIndex !== undefined) {
      const nextId = queue[songIndex + 1];
      if (nextId) {
        dispatch(handleNextSongThunk({ songId: nextId }));
      } else {
        dispatch(handleNextSongThunk({ songId: queue[0] }));
      }
    } else {
      dispatch(getSongThunk({ songId: currentSong.next }));
    }
  };

  const loadPrevAndNextOrRandom = () => {
    if (!song.id && queue.length > 0) {
      dispatch(handleNextSongThunk({ songId: queue[0] }));
      return null;
    } else if (!song.prev || !song.next) {
      const currentSong = songs.find((s) => s.id === song.id);
      if (currentSong && currentSong.prev && currentSong.next) {
        return currentSong;
      } else {
        dispatch(handleNextSongThunk({ songId: queue[0] }));
        return null;
      }
    }
  };

  const handleMix = () => {
    dispatch(mixSongs(!settings.mix));
    dispatch(setPlayerSetting({ mix: !settings.mix }));
  };

  const handleRepeat = () => {
    let payload;
    if (settings.repeatOneSong) {
      payload = { repeat: false, repeatOneSong: false };
    } else if (settings.repeat) {
      payload = { repeat: false, repeatOneSong: true };
    } else {
      payload = { repeat: true, repeatOneSong: false };
    }
    dispatch(setPlayerSetting(payload));
  };

  const handleChangeTime = (time) => {
    setTime(time);
    audioRef.current.currentTime = time;
  };

  const handleChangeVolume = (e) => {
    setVolume(e.target.value);
    audioRef.current.volume = e.target.value / 100;
  };

  const handleMuteAndUnmute = () => {
    if (audioRef.current.muted) {
      setVolume(100);
      audioRef.current.volume = 1;
      audioRef.current.muted = false;
    } else {
      setVolume(0);
      audioRef.current.volume = 0;
      audioRef.current.muted = true;
    }
  };

  return {
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
  };
};

export default useAudioPlayer;
