import React from 'react';
import { Card, CardContent, CardMedia, Skeleton } from '@mui/material';
import Typography from '@mui/material/Typography';

const AlbumsDataGridMockItem = () => {
  return (
    <Card className={'items-center p-1 hover:bg-gray-100'} sx={{ maxWidth: 225, minWidth: 225 }}>
      <CardMedia className={'relative p-3'}>
        <Skeleton
          variant="rounded"
          width={200}
          height={200}
          sx={{
            borderRadius: 2
          }}
        />
      </CardMedia>
      <CardContent>
        <Typography variant="h6" component="div" align="left" noWrap>
          <Skeleton variant="text" />
        </Typography>
      </CardContent>
    </Card>
  );
};

export default AlbumsDataGridMockItem;
