import { Injectable } from '@angular/core';
import { UploadFile } from 'ng-zorro-antd';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UploadFileService {
  constructor(private http: HttpClient) {}

  public uploadChunkFile(
    file: UploadFile,
    index: string,
    total: number,
    chunkSize: number,
    data: string | ArrayBuffer
  ): Observable<any> {
    const params = new HttpParams()
      .append('name', file.name)
      .append('size', file.size.toString())
      .append('currentChunkIndex', index)
      .append('totalChunks', total.toString());

    const headers = new HttpHeaders();

    headers.append('Content-Type', 'application/octet-stream');
    return this.http.post(
      '/api/stream?' + params.toString(),
      { data },
      { headers }
    );
  }

  public uploadStream(files: FormData): Observable<any>{
    const headers = new HttpHeaders();
    headers.append('Content-Type', 'multipart/form-data');
    return this.http.post('/api/stream', files, { headers, reportProgress: true });
  }

  public uploadFormData(files: FormData): Observable<any>{
    const headers = new HttpHeaders();
    headers.append('Content-Type', 'multipart/form-data');
    return this.http.post('/api/multipart', files, { headers, reportProgress: true });
  }
}
